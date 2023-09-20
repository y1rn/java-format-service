# google-java-format-service

## Introduction

A JSONRPC service to format codes in [google-java-format](https://github.com/google/google-java-format)

Depends on google-java-format **1.17.0**

## Requirements

- Java >= `17`

## API

### format

Format input codes with its parameters

- Request body:

  ```json
  {
    "styleName": "GOOGLE|ASAP",
    "skipSortingImports": true | false,
    "skipRemovingUnusedImports": true | false,
    "data": "java code"
  }
  ```
