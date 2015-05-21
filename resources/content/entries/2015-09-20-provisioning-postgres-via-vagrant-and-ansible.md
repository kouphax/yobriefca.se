---
date: 2015-05-20T00:00:00Z
title: "Provisioning Postgres via Vagrant and Ansible"
published: true
categories: [Vagrant, Ansible, Postgres]
type: article
external: false
---

This article covers a simple workflow for setting up a [Vagrant](http://vagrantup.com) instance running [Postgres](http://postgresql.org) provisioned by [Ansible](http://ansible.com). The first time I did this I found that most articles didn't cover everything and I had to look around to solve a few minor issues.

> It's worth noting that I can avoid all this and use Nic Ferriers very useful [Postgres Dev Box](http://www.pgdevbox.com/).  This work may be unnecessary but it's still fun - __Funecessary__

I'm going to make an assumption that you know what both Vagrant and Ansible are and you understand the basic premise of how they work.  I'll also assume that you have ansible installed on the host machine (Ansible is purely SSH and doesn't need agents installed on target machines).

## Vagrantfile

We start with the `Vagrantfile`.  This is the file that tells Vagrant what VM base box to use (Ubuntu Precise 32), what ports should be forwarded to the host (5432 - the default Postgres port) and what provider should be used to provision the VM once it has been brought up (Ansible).

If you already have a local instance of Postgres running you'll want to shut it down or you'll get port collisions and Vagrant will complain.

```ruby
Vagrant.configure("2") do |config|
    config.vm.box = "precise32"
    config.vm.box_url = "http://files.vagrantup.com/precise32.box"

    config.vm.network "forwarded_port", guest: 5432, host: 5432

    config.vm.provision "ansible" do |ansible|
        ansible.playbook = "playbook.yml"
    end
end
```

## playbook.yml

Ansible provisioning in Vagrant accepts, at the minimum, a playbook to run and the provider takes care of ensuring that ansible is called correctly.  Our Vagrantfile above is configured to point to `playbook.yml` that sits alongside the `Vagrantfile` so we need to create that.

```
- hosts: all
  sudo: yes
  gather_facts: no
  tasks:
    - name: add keyserver to apt
      command: apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8
    - name: add custom postgres repo to apt
      lineinfile: dest=/etc/apt/sources.list.d/pgdg.list
                  line="deb http://apt.postgresql.org/pub/repos/apt/ precise-pgdg main" state=present
                  create=yes

- hosts: all
  sudo: yes
  gather_facts: no
    - name: ensure apt cache is up to date
      apt: update_cache=yes
    - name: ensure packages are installed
      apt: name={{item}}
      with_items:
          - python-software-properties
          - software-properties-common
          - libpq-dev
          - python-psycopg2
          - postgresql-9.4

- hosts: all
  sudo: yes
  sudo_user: postgres
  gather_facts: no

  handlers:
    - name: restart postgresql
      service: name=postgresql state=restarted

  tasks:
    - name: postgresql should listen on all ports
      lineinfile: dest=/etc/postgresql/9.4/main/postgresql.conf
                  regexp="^listen_addresses"
                  line="listen_addresses = '*'" state=present

    - name: postgresql should allow access to host
      copy:
        dest: /etc/postgresql/9.4/main/pg_hba.conf
        content: |
          local   all   postgres   peer
          local   all   all        peer
          host    all   all        0.0.0.0/0   md5
      notify: restart postgresql

- hosts: all
  sudo: yes
  sudo_user: postgres
  gather_facts: no

  vars:
    dbname: vagrant
    dbuser: vagrant
    dbpassword: vagrant

  tasks:
    - name: ensure database is created
      postgresql_db: name={{dbname}}

    - name: ensure user has access to database
      postgresql_user: db={{dbname}} name={{dbuser}} password={{dbpassword}} priv=ALL
```

It probably best to tease apart what each part of this playbook does.  I've broken the playbook out into four distinct sections that perform specific related tasks to make this easier to explain,

- Add the Postgres apt repository to allow us to get the latest release of Postgres (`apt` by default will install `9.1` but I want `9.4`
- Update the `apt` cache and install the necessary packages (ultimately `postgresql-9.4` but we need a few base packages as well.

By this point Postgres will be installed and a `postgres` user created but if you try and connect to the instance from outside the VM it still wont work.  We have some additional work to do.  This is exactly what the next block does.

- Update the `postgresql.conf` file to allow Postgres to listen on all ports and update `pg_hba.conf` to allow the host machine to connect to Postgres. _NB_ these rules are very lax, for a bit of local development work this is fine but it would be best to tighten these up for more serious work.  The service should be restarted after both of these tasks.

So now we have the ability to connect to the database from the host machine we also want to set up a dedicated database and user.

- Create a `vagrant` database and create and associate a `vagrant` user to this database.

If we save both files and run `vagrant up` we will end up with a provisioned Vagrant box running Postgres that can be access from the host machine on port 5432.
