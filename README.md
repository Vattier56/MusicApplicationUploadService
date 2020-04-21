# Music Application Upload Service
 Upload Service allows to save images and audio files in S3 cloud.

## Table of contents
* [General info](#general-info)
* [Requirements](#requirements)
* [Setup](#setup)

## General info
By default, the service runs at ``` http://localhost:8100 ```

Available upload options: 
- Song/Album cover image
- User avatar
- Audio file
- Audio file with metadata fetching (requires [Music Application Audio Service](https://github.com/Vattier56/MusicApplicationAudioService) running)




## Requirements:
- Java version 1.8
- Apache Maven version 3.6.3
- Amazon account with S3 service
- Git repository with S3 configuration 


## Setup

To run this project, install it locally using maven:

```
$ mvn package
$ cd target
$ java -jar <projectName>
```

## Author
* Piotr Piasecki - [Vattier56](https://github.com/Vattier56)
