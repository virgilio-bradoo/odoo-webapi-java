# odoo-webapi-java
This is a small, self-contained Gradle based Java project that have some Odoo 
Web API call code examples.

* The API calls are made using Apache's XMLRPC-Client library
* Each API call return plain simple objects that are converted to Json using 
Gson library
* It connects to Odoo demo server by default

It's important to note that the API responses are always plain objects (POJO) 
or Array/Map of POJOs which can be used with any Collections combinations and
are also Serializable, JSON convertible and can be used to create 
representational objects within any project, some examples:
* Object
* asList((Object[])
* (Map) ((Object[])
* Map<String, Map<String, Object>>

To send data, use Maps 
 
