Front end Data server
=====

This is a guide running and make simple modifications to the front end server.

## About

The fronent server is based off the library `reveal.js`, which is build on Node.
Also, it uses Jade as a templating system and Backbone as MCV for the javascript.
Modules are loaded using require.js

## Run

In order to run the server, type `grunt serve` in the `presentation` folder.
The presentation will be accessible at http://127.0.0.1:8000.

## Modify the presentation

In order to change what data you want to display and how, you can 
modify the file `settings.js`. This will be a mirrow of the file
`conf/settings.json`,
 only exception is that the settings will be applied to the fronted.
