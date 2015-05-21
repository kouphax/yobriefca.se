---
date: 2015-05-22T00:00:00Z
title: "Provisioning Postgres via Docker"
published: true
categories: [Postgres, Docker]
type: article
external: false
---

[Previously](/blog/2015/05/20/provisioning-postgres-via-vagrant-and-ansible/) we looked at [provisioning Postgres via Vagrant and Ansible](/blog/2015/05/20/provisioning-postgres-via-vagrant-and-ansible/) and in the spirit of trying something a bit different lets do the same thing using [Docker](http://docker.io).  

> This was new territory for me but this guide will get you far enough for hacking around on a project.  If this is an awful way of doing things let me know.

Same rules apply - we want to provision a project level Postgres instance listening on host port `5432` with a new database and user.  

## boot2docker

> If you're not using `boot2docker` (if you're on a supported Linux platform) you can skip this bit

Docker is primarily a Linux based tool but there are wrappers for Windows and OSX.  Since I'm using a Mac I'll also use [boot2docker](http://boot2docker.io/) that uses a Linux VM and proxies requests to the VM.

I'm going to assume that at this point we have `docker` and, if necessary, `boot2docker` installed.  If you're using OSX `brew` is your friend (`brew install boot2docker`).

With `boot2docker` installed we need to start it.

```
boot2docker up
```

After this has started you should see an information message about setting the necessary environment variables to allow docker and boot2docker to talk correctly.  There is a quick way to do all this provided by `boot2docker`

```
$(boot2docker shellinit)
```

## Dockerfile

The `Dockerfile` specifies the rules for building a docker image that we can use to create container instances from.  I'll just dump ours here and break it down after.

```
FROM ubuntu

# add postgresql release repository to apt
RUN apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ precise-pgdg main" > /etc/apt/sources.list.d/pgdg.list

# install the necesarry packages
RUN apt-get update && \
  apt-get install -y \
    python-software-properties \
    software-properties-common \
    postgresql-9.4

# run the rest as the postgres user
USER postgres

# create the database and user
RUN /etc/init.d/postgresql start && \
    psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" && \
    createdb -O docker docker

# ensure host can connect to postgres correctly
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/9.4/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/9.4/main/postgresql.conf

# expose the 5432 port to outside the container
EXPOSE 5432

# set the default command to run when starting the container
CMD ["/usr/lib/postgresql/9.4/bin/postgres", \
      "-D", "/var/lib/postgresql/9.4/main",  \
      "-c", "config_file=/etc/postgresql/9.4/main/postgresql.conf"]
```

The flow is somewhat similar to the [previous](/blog/2015/05/20/provisioning-postgres-via-vagrant-and-ansible/) post,

- We want to use the latest version of Postgres so we add the offical Postgres repo (and pgp-key) to the `apt` catalogue.
- We install all the necessary packages
- Then we switch to the newly created `postgres` user to perform the rest of the operations
- In `RUN` we start the database, create the user `docker` with password `docker` and finally create the `docker` database.
- We then update the necessary `*.conf` files to allow us to connect to the Postgres instance outside of the container.
- Next we `EXPOSE` the 5432 port to the outside world (in a `boot2docker` world this exposes it from the container to the VM, we still have a bit of work to do to get it exposed to the actual host.
- Finally we stipulate how to start the Postgres instance when the container starts.

## Running

So now we have specified the rules in the `Dockerfile` (this is somewhat similar to the Ansible playbook concept, sort of, but not quite) we want to build a versioned image from this,

```
docker build --tag postgres:9.4 --rm  .
```

Running this from the same directory as the `Dockerfile` will build an image called `postgres` tagged with `9.4`.  The `--rm` command tells docker to remove the intermediary images created.

We can see that our image has been created using the `docker images` command

```
>  docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             VIRTUAL SIZE
postgres            9.4                 f25b0a52a81b        26 minutes ago      358.8 MB
```

Next we need to take this image and run it as a container,

```
docker run -p 5432:5432 --name postgres-instance -d postgres:9.4
```

Lets have a look at the option here,

- `-p 5432:5432` publishes the `EXPOSE`d `5432` port effectively making it accessible outside the container.
-  `--name postgres-instance` is the friendly name we want to give the container
- `-d postgres:9.4` is the image name and tag to create the container from

We can verify the container instance is running using the `docker ps` command

```
> docker ps
CONTAINER ID        IMAGE               COMMAND                CREATED             STATUS              PORTS                    NAMES
f54de34e1308        postgres:9.4        "/usr/lib/postgresql   22 minutes ago      Up 22 minutes       0.0.0.0:5432->5432/tcp   postgres-instance
```

At this point we should be able to access the Postgres instance via the IP of the VM.  To ge the IP we can use the `ip` command of `boot2docker`

```
> boot2docker ip
192.168.59.103
```

## boot2docker Port Forwarding

If we are using `boot2docker` we still wont be able to access the instance via localhost.  As already mentioned `boot2docker` transparently uses a Linux VM (via VirtualBox) to run docker within.  By default this VM doesn't forward any ports to the host so we need to tell `boot2docker` (via `VBoxManage`) to open that port.

```
VBoxManage modifyvm "boot2docker-vm" --natpf1 "postgres-port,tcp,127.0.0.1,5432,,5432"
```

This command tells VirtualBox to forward the 5432 TCP port of the `boot2docker` VM to the host.

At this point we should be able to access the Postgres instance via `localhost` on the host as well as the VMs IP address directly.