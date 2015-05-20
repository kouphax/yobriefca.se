---
date: 2015-05-20T00:00:00Z
title: "Basic Login/Logout with Luminus"
published: false
categories: [Clojure]
type: article
external: false
---

Starting out a project with [Luminus](http://luminusweb.net) I always invariably find myself poking around the documentation to try and get a simple login/logout workflow implemented.  All the knowledge is there but because it covers a bunch of aspects of  [Luminus](http://luminusweb.net) it all isn;t in the one place.  This article documents an approach to give us a simple login/logout process that will start out barebones and will evolve over the course of the article to cover,

- Simple hardcoded login/logout process (views and session management)
- Integrating user lookup with a database
- New User Registration

## Generating the Project

First things first we need a starting point

    lein new luminus auth-demo +postgres +auth

## Setting up the database (MAKE THIS COME LATER)

I don't know about you but I really hate installing a myriad of DBs directly onto my local machine so 

```ruby
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
    config.vm.box = "precise32"
    config.vm.box_url = "http://files.vagrantup.com/precise32.box"

    config.vm.network "forwarded_port", guest: 5432, host: 5432

    config.vm.provision "ansible" do |ansible|
        ansible.playbook = "playbook.yml"
    end
end
```

```yml

- hosts: excluded
  sudo: yes
  gather_facts: no
  tasks:
  - name: ensure apt cache is up to date
    apt: update_cache=yes
  - name: ensure packages are installed
    apt: name={{item}}
    with_items:
        - postgresql
        - libpq-dev
        - python-psycopg2

- hosts: all
  sudo: yes
  sudo_user: postgres
  gather_facts: no

  vars:
    dbname: vagrant
    dbuser: vagrant
    dbpassword: vagrant
    pg_hba_template: |
      local   all   postgres   peer
      local   all   all        peer
      host    all   all        0.0.0.0/0   md5

  handlers:
    - name: reload postgresql
      service: name=postgresql state=reloaded

    - name: restart postgresql
      service: name=postgresql state=restarted
  tasks:
    - name: ensure database is created
      postgresql_db: name={{dbname}}

    - name: ensure user has access to database
      postgresql_user: db={{dbname}} name={{dbuser}} password={{dbpassword}} priv=ALL

    - name: ensure user does not have unnecessary privilege
      postgresql_user: name={{dbuser}} role_attr_flags=NOSUPERUSER,NOCREATEDB

    - name: postgresql should listen on all ports
      lineinfile: dest=/etc/postgresql/9.1/main/postgresql.conf
                  regexp="^listen_addresses"
                  line="listen_addresses = '*'" state=present

    - name: postgresql should allow access to host
      copy:
        dest: /etc/postgresql/9.1/main/pg_hba.conf
        content: "{{ pg_hba_template }}"
      notify: restart postgresql
```

- 