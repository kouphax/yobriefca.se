# Briefcase

Briefcase is the post-CMS engine that powers http://yobriefca.se.  It is built on Clojures Stasis library and will generate static files of the current state of yobriefca.se.  Originally written in Ruby and Middleman the conversion was undertkaen as a learning experience and experiment.  It stayed because it offered greater flexibiltiy at the cost of a tiny bit more code and reduction in overall generation of speed.

Feel free to take stuff from it if thats your kind of thing.

## Running

In dev mode you can run the site with the `lein-ring` plugin

```bash
lein ring server
```

To export the site you can use the alias that has been created

```bash
lein build-site
```

Which produces output in the `/build` folder under the project root.
