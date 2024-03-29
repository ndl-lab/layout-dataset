NDL-DocLデータセット(資料画像レイアウトデータセット)
==================
Click [here](./readme_en.md) for the README(English version)

## データセットの概要

NDL-DocLデータセットは以下のURLから公開しています。<br/>
現在(2019年12月9日)の最新バージョンは1.0です。<br/>
古典籍資料：https://lab.ndl.go.jp/dataset/dataset_kotenseki.zip

明治以降刊行資料:https://lab.ndl.go.jp/dataset/dataset_kindai.zip


1.概要
----

### 1.1 データセットの提供元

NDL-DocLデータセットは、国立国会図書館デジタルコレクション（以下「デジコレ」といいます。）&lt;[*http://dl.ndl.go.jp/*](http://dl.ndl.go.jp/)&gt;が提供している資料画像データの中から、「古典籍資料」「明治期以降刊行資料」の2種類を公開しています。

アノテーションデータについては当館職員が新規に作成したものを公開しています。

### 1.2 データセットの内訳

NDL-DocLデータセット(ver1.0)の内訳は以下のとおりです。

  |資料の種類                   | 画像数
  |-------------------|----------
  |古典籍資料 (https://lab.ndl.go.jp/dataset/dataset_kotenseki.zip)          |1,219画像
  |明治期以降刊行資料(https://lab.ndl.go.jp/dataset/dataset_kindai.zip)    |1,071画像

### 1.3 データセットの権利
「PDM（パブリック・ドメイン・マーク）」&lt; https://creativecommons.org/publicdomain/mark/1.0/deed.ja &gt;

※古典籍資料、明治期以降刊行資料ともに著作権保護期間満了資料のみを対象としています。

NDL-DocLデータセットは、自由な二次利用が可能です。ただし、二次利用に際しては、次の事項へのご配慮をお願いいたします。これらのお願いは法的な契約ではありませんが、できる限りご留意の上でご利用くださるよう、ご協力をお願いします。

- データを編集・加工等して利用する場合は、それを行ったことを記載してください。編集・加工等を、元となる作品・原資料の作者や当館が行なったかのような態様で公表しないようご留意ください。
- 当該データが自由に二次利用可能であることの表記を保持してください。
- 元となる作品や、その作者の名声を傷つける形での利用は行わないようご留意ください。また、元となる作品に関わる文化やコミュニティへの配慮を行ってください。
- 著作権以外の権利（著作者人格権、著作隣接権、肖像権、パブリシティ権、プライバシー権、商標権等）にも留意し、関連法令を遵守してください。
- 論文等に利用する場合には、先行研究や後続研究と比較を容易にするためNDL-DocLデータセットとバージョンの明記にご協力ください。


2 データセットの説明
------------------

### 2.1 データセットの構成

NDL-DocLデータセットは画像ごとに以下の2種類を含みます。

(1) 資料画像(jpeg画像)

(2) アノテーションデータ(xml形式)

**ディレクトリ命名規則**

各ディレクトリ名は資料のPID(Persistent Identifier)を意味し、例えば2534020であれば

http://dl.ndl.go.jp/info:ndljp/pid/2534020

とすることで当該資料のデジコレ上のURLとしてアクセスできます。

**ファイル命名規則**

命名規則は
(PID)_(コマ番号)
となっています。
例えば2534020_0003は、http://dl.ndl.go.jp/info:ndljp/pid/2534020
のコマ番号3を意味します。

**PIDと資料名の対応表**

デジコレの書誌データは以下から提供しています。

https://www.ndl.go.jp/jp/dlib/standards/opendataset/index.html

各メタデータは以下から、

http://dl.ndl.go.jp/files/dataset/dataset_201907_k_internet.zip


http://dl.ndl.go.jp/files/dataset/dataset_201907_t_internet.zip

それぞれダウンロード可能です。


### 2.2 アノテーションデータの形式

Pascal
VOC形式でレイアウトの矩形情報とラベル名を記述したxmlを画像ごとに提供しています。1600\*1200サイズの画像から資料全体の含まれる矩形領域を記述した例を以下に挙げます。

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

同一資料内に複数のレイアウトが存在する場合、xml内にobjectを複数記述することで対応します。

付与されるラベル情報は「古典籍資料」と「明治期以降刊行資料」で異なります。「3各資料に関する詳細情報」を参照してください。

3 各資料に関する詳細情報
----------------------

### 3.1 古典籍資料について

#### (1) 特徴

明治期より前に出版された出版物であり、浮世絵や和書・漢籍資料が含まれます。

浮世絵の中に文字が書き込まれているなど、イラスト内部に文字ラインが入ることを許容しています。

#### (2) 予測対象となるレイアウトのラベル

  |No  | ラベル名          |説明
  |----| -----------------| --------------------------------------
  |1   | 1\_overall       | 資料範囲全体
  |2   | 2\_handwritten   | くずし字の文字ライン
  |3   | 3\_typography    | 楷書体・行書体の文字ライン
  |4   | 4\_illustration  | イラスト
  |5   | 5\_stamp         | 印影（蔵書印等）

<img src="https://github.com/ndl-lab/layout-dataset/blob/master/images/kotenseki_ss.PNG" alt="kotenseki sample image" title="screen shot">

### 3.2 明治期以降刊行資料について

#### (1) 特徴
明治期以降に出版された、冊子の形態をとる出版物であり、マイクロ資料をデジタル化した資料など、強いノイズの乗った資料が多く存在します。

多くは昭和前期より前に刊行された資料からなりますが、一部戦後に刊行された刊行物を含みます。

#### (2) 予測対象となるレイアウトのラベル

  |No   |ラベル名          |説明
  |---- |-----------------| ---------------------------------------------
  |1    |1\_overall       | 資料範囲全体
  |2    |4\_illustration  | イラスト（写真を含む）
  |3    |5\_stamp         | 印影（蔵書印等）
  |4    |6\_headline      | 見出し
  |5    |7\_caption       | 図表見出し
  |6    |8\_textline      | 6\_headline, 7\_caption以外の文字ライン
  |7    |9\_table         | 表

※ラベル名の先頭の数字は両資料で通し番号

<img src="https://github.com/ndl-lab/layout-dataset/blob/master/images/kindai_ss.PNG" alt="kindai sample image" title="screen shot">




4 過去のバージョン情報
----------------------
20191209 ver1.0提供開始<br/>
[古典籍資料](https://lab.ndl.go.jp/dataset/legacy/dataset_kotenseki_ver1.0.zip)
[明治期以降刊行資料](https://lab.ndl.go.jp/dataset/legacy/dataset_kindai_ver1.0.zip)

20191108 ベータ版提供開始

