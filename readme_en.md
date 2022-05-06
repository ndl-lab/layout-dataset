NDL-DocL Datasets
==================

## Overview of the datasets

NDL-DocL datasets can be accessed via the following URL. <br/>
The latest version is 1.0 (as of Dec. 9, 2019). <br/>

Rare Books and Old Materials: https://lab.ndl.go.jp/dataset/dataset_kotenseki.zip
Materials published after 1868: https://lab.ndl.go.jp/dataset/dataset_kindai.zip


1 Overview
----

### 1.1 Provider of datasets

NDL-DocL datasets include "Rare Books and Old Materials" and "Materials published after 1868" extracted from image data in the National Diet Library Digital Collections &lt;[*http://dl.ndl.go.jp/*](http://dl.ndl.go.jp/)&gt;

Annotated data have been newly made by the NDL.

### 1.2 Contents of dataset

NDL-DocL datasets (ver1.0) include following images:

  |Material type                   | Number of image
  |-------------------|----------
  |Rare Books and Old Materials (https://lab.ndl.go.jp/dataset/dataset_kotenseki.zip)          |1,219
  |Materials published after 1868 (https://lab.ndl.go.jp/dataset/dataset_kindai.zip)    |1,071

### 1.3 Right of datasets
PDM (Public Domain Mark) &lt; https://creativecommons.org/publicdomain/mark/1.0/deed.en &gt;

N.B. Both datasets contain only materials for which the copyright protection period has expired.

The NDL-DocL dataset can be freely used for secondary use. However, we would like to ask you to take the following points into consideration when using the data. These requests are not legal agreements, but we ask for your cooperation in using the data with as much care as possible.

- If you edit or modify the data, please mention that you have done so. Please do not publish the edited or modified data in a way that gives the impression that it was done by the author of the original work or by the NDL.
- Please retain the statement that the data is freely available for secondary use.
- Please do not use the work in a way that could damage the reputation of the original work or its author. Also, please be respectful to the culture and community associated with the original work.
- Please pay attention to rights other than copyright (moral rights, neighboring rights, portrait rights, publicity rights, privacy rights, trademark rights, etc.) and comply with the relevant laws.
- When using the data in a research paper or other publications, please mention that the data was obtained from the NDL-DocL dataset and its version, in order to facilitate comparison with previous and subsequent studies.


2 About datasets
------------------

### 2.1 Structure of datasets

Each image has:

(1) image of the material (jpeg)

(2) annotated data (xml)

**Naming rules of directories**

Each directory name refers to the PID (Persistent Identifier) of the material. For example, if the directory name is "2534020", the URL of the material in the NDL Digital Collection can be composed and accessed as follows:

http://dl.ndl.go.jp/info:ndljp/pid/2534020

**Naming rules of files**

The file name is structured by the following rule.
(PID)_(frame number)
For example, "2534020_0003" means the frame number 3 of http://dl.ndl.go.jp/info:ndljp/pid/2534020.


**Correspondence of PIDs and titles**

The bibliographic data of the NDL Digital Collections are available at the following URL:
https://www.ndl.go.jp/jp/dlib/standards/opendataset/index.html

Metadata of NDL-DocL datasets can be downloaded respectively from the following URLs:
http://dl.ndl.go.jp/files/dataset/dataset_201907_k_internet.zip
http://dl.ndl.go.jp/files/dataset/dataset_201907_t_internet.zip


### 2.2 Format of annotated data

An xml file in Pascal VOC format describing the rectangle information of the layout and the label name is provided for each image. Below is an example describing the rectangular area that covers the entire document from a 1600*1200 size image.

```xml
<?xml version="1.0"?>
<annotation>
  <folder>kotenseki</folder>
  <filename>3510690_0036</filename>
  <path>kotenseki/3510690/3510690_0036.jpg</path>
  <source>
    <database>NDLDocL</database>
  </source>
  <size>
    <width>1600</width>
    <height>1200</height>
  </size>
  <segmented>0</segmented>
  <object>
    <name>1_overall</name>
    <bndbox>
      <xmin>300</xmin>
      <ymin>149</ymin>
      <xmax>1299</xmax>
      <ymax>1080</ymax>
    </bndbox>
  </object>
</annotation>
```

If there are multiple layout possibilities in the same document, several objects are described in the xml file.
The label information added to "Rare Books and Old Materials" and "Materials published after 1868" are different. Please refer to "3 Detailed information for each dataset".


3 Detailed information for each dataset
----------------------

### 3.1 About "Rare Books and Old Materials"

#### (1) Features

These are materials published before the beginning of the Meiji era (1868) and include ukiyoe, Japanese books, and Chinese books. There may be text lines inside illustrations, such as text written in ukiyo-e.

#### (2) Layout labels for target of prediction

  |No.  | Label          |Description
  |----| -----------------| --------------------------------------
  |1   | 1¥_overall       | Document area
  |2   | 2¥_handwritten   | Text line of handwritten script (Kuzushi-ji)
  |3   | 3¥_typography    | Text line of regular and semi-cursive script
  |4   | 4¥_illustration  | Illustration
  |5   | 5¥_stamp         | Seal and stamp (e.g. ex-libris stamp)

<img src="https://github.com/ndl-lab/layout-dataset/blob/master/images/kotenseki_ss.PNG" alt="kotenseki sample image" title="screen shot">

### 3.2 About "Materials published after 1868"

#### (1) Features
These are publications in the form of book from the Meiji era onward (1868-). There are many materials that contain strong noise, such as documents that have been digitized from microfilms or microfiches.
Most of them were published before the early Showa period (before 1945), but some of them were published after World War II.

#### (2) Layout labels for target of prediction

  |No.   |Label          |Description
  |---- |-----------------| ---------------------------------------------
  |1    |1¥_overall       | Document area
  |2    |4¥_illustration  | Illustration (incl. photographies)
  |3    |5¥_stamp         | Seal and stamp (e.g. ex-libris stamp)
  |4    |6¥_headline      | Headline
  |5    |7¥_caption       | Caption
  |6    |8¥_textline      | Text line other than 6¥_headline and 7¥_caption
  |7    |9¥_table         | Table

N.B. The first digit of the label name is the common for both datasets.

<img src="https://github.com/ndl-lab/layout-dataset/blob/master/images/kindai_ss.PNG" alt="kindai sample image" title="screen shot">




4 Version history
----------------------
20191209 ver1.0<br/>
[Rare Books and Old Materials](https://lab.ndl.go.jp/dataset/legacy/dataset_kotenseki_ver1.0.zip)
[Materials published after 1868](https://lab.ndl.go.jp/dataset/legacy/dataset_kindai_ver1.0.zip)

20191108 beta version


