# Book Layout Annotator

<img src="https://github.com/ndl-lab/layout-dataset/images/ss.png" alt="annotation editor image" title="screen shot">

## back

Backend is spring-boot-based Single Jar Application.

### required

- JDK(1.8 or later)
- Apache Maven
- [Elasticsearch](https://www.elastic.co/) (worked on 7.3.2) as Database

### build

```
mvn clean install
```

### setup

#### 1. elasticsearch

After install & run elasticsearch, run following to create index

```
cd target
java -jar tugidigi-annotator-0.1.jar batch create-index all
```

#### 2. annotation type

To register annotation rule

```
java -jar tugidigi-annotator-0.1.jar batch add-type ../sample/type.json
```

See sample/type.json

#### 3. target data

To register target image data, you need following directory structure. See sample/data directory

- target-dir
    - book-dir
        - binder.json : Metadata (1 per book)
        - xml-dir : Layout data (Pascal VOC format) if any. XML file name must be correspond to image file name.
            - xxxx_01.xml
            - xxxx_02.xml
            - ...
        - image-dir : Image Data. Image File Name must be UNIQUE in system. 
          - xxxx_01.jpg
          - xxxx_02.jpg
          - ...

```
java -jar tugidigi-annotator-0.1.jar batch add-image ../sample/data true
```

This command iterate directories in sample/data.
Each directory represents 1 book, and program try to find binder.json in each directory.

### run

```
java -jar tugidigi-annotator-0.1.jar web
```

Application is running on [http://localhost:9985/app/](http://localhost:9985/app) by default

### manage

#### export annotated data

```
java -jar tugidigi-annotator-0.1.jar batch export all output_dir
```

"all" can be changed to registered annotation type name.

#### update annotated data

```
java -jar tugidigi-annotator-0.1.jar batch update-layout xml_dir
```

xml_dir must contains xml files(Pascal VOC format), which has [image id].xml in name.

## front

Frontend is SPA powered by vue & vue-router.

### required

- [Node.js](https://nodejs.org/) (v10.15.0 or later)

### install

```
npm install
```

### build

```
npm build
```
