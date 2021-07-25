# PoC for IMDB with REST API

## Quick start

1. Run **mvn clean package**
2. From target directory please execute in terminal: **java jar IMDB-0.0.1.jar**
3. Import postman collection from **postman** directory and start executing scripts


## Configuration
    keyValueConcurrencyLevel: 32 //concurrecny level for ConcurrentHashMap used for key-value results
    keyListConcurrencyLevel: 32 //concurrecny level for ConcurrentHashMap used for key-list results
    listsLocksInitialCount: 2 //initial setup for amount of locks created for syncronized operations over key-list objects