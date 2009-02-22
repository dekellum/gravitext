
* http://github.com/dekellum/gravitext

The gravitext tree contains the source for several distinct gems (per
rubyforge conventions), in independent directories. 

Github:

% git remote add origin git@github.com:dekellum/gravitext.git 

Rubyforge:

Release a gem:

    VERSION=x.y.z jrake release publish_docs post_news

Create package under gravitext project:

    (rubyforge login)
    rubyforge create_package gravitext <package-name>

