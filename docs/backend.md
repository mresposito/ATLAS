Backend Data generator
=====

This document is meant as a guide to using the backend
that generates the data.

## Concepts

The backend works using a DSL that you can configure easily from a json file.
Here are the components that will allow you to specify a new series to
run the analysis against.

 * Semester: Which semesters you want to run the data on
 * Aggregations: This is used to input extra data in the system. For example,
    in the default config file,
    you will see that we feed in the `listOfClasses.tsv`.
    You can also specify a filter on that list. You must specify a `tag`
    once you use an aggregation
 * Columns: This is the core of the application.
    Columns allow you to access the database using a query,
    which is specified in the field `query`.
    Columns and name are the only required field in a series.
    Without columns, the application will not run.

## Configuration

The main configuration file is contained in `conf/settings.json`.
This file is read by the backend to parse data.

You can also specify more options, such as the input directory,
database configuration and other goodies
at `src/main/resources/application.conf`
