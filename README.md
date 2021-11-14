# weighted-fleiss-kappa
This ia a library for estimating inter-annotator agreements by weighted Fleiss' kappa coefficient. See the reference for the defnition of weighted Fleiss' kappa coefficient.

# Usage
For evaluating inter-annotator agreements, a following command line tool is provided:

>`java -Dfile.encoding=UTF-8 -cp [CLASSPATH] jp.co.d_itlab.dbdc.tool.IAATool -s maa -dic [DIC_DIR] -c INT -a [ANNOTATORS] -i [INPUT_DIR] (-l [LOCALE])`

Option Description:

- cp - paths the iaa.jar (placed in "jar" directory) and other jar files of dependent libraries specified in the Requirements.
- s - command ("maa" for estimating inter-annotator agreements by weighted Fleiss' kappa coefficient.)
- dic - path to the directory where the text files which define error categories are defined. (see "[res/dic](https://github.com/htsukahara/weighted-fleiss-kappa/tree/main/res/dic)" directory.)
- a - IDs of annotators to be evaluated. (concatenated by commas, e.g., W1,W2,W3 for annotators W1, W2, W3.
- i - path to the annotated data
- l - specifies the language of data. (ja: Japanese(default), en: English)

Launch setting samples are shown in the batch files under the "[sample](https://github.com/htsukahara/weighted-fleiss-kappa/tree/main/sample)" directory. For executing those batch files,  the dependent libraries should be placed in "picocli", "poi", "log4j" directories under "jar" directory, respectively.

## Directory Layout of annotated files
The annotation of error category is assumed to be worked using excel files. (Like a template in "[res/excel](https://github.com/htsukahara/weighted-fleiss-kappa/tree/main/res/excel)" directory.) The annotated files should be distributed in the following directory layout:

<pre>
[data]   
 │
 └─ [annotator-id]
 │     │
 │     └─ [trial-d]
 │     │     │
 │     │     │─ [trial-id_dialogue-system-id].xlsm
 │     │     └─ ...
 │     └─ ...
 └─ ...
</pre>

See examples in the "[sample/data](https://github.com/htsukahara/weighted-fleiss-kappa/tree/main/sample/data)" directory.

<!--The template of those excel files is placed in "[res/excel](https://github.com/htsukahara/weighted-fleiss-kappa/tree/main/res/excel)" directory".-->

# Download

The complied JAR file is provided: [iaa-1.0.0.jar](https://github.com/htsukahara/weighted-fleiss-kappa/tree/main/jar)

# Requirements
1. JRE v1.8 or above version installed
1. Dependent Libraries
    1. PicoCli - https://github.com/remkop/picocli
        
        Download the latest(4.6.2 or above) JAR file and place it under jar/poicocli:
        - picocli-4.6.2.jar

    1. Apache POI - https://poi.apache.org/
        
        Download the latest(5.0.1 or above) binary distribution, expand it and place following JAR files under jar/poi:
        - poi-5.1.0.jar
        - poi-ooxml-5.1.0.jar
        - poi-ooxml-lite-5.1.0.jar
        - lib/commons-collections4-4.4.jar
        - lib/commons-io-2.11.0.jar
        - lib/log4j-api-2.14.1.jar
        - ooxml-lib/commons-compress-1.21.jar
        - ooxml-lib/commons-logging-1.2.jar
        - ooxml-lib/curvesapi-1.06.jar
        - ooxml-lib/xmlbeans-5.0.2.jar

    1. Log4j Core - https://logging.apache.org/log4j/2.x/log4j-core/
        
        Downloiad the latest(2.14.1 or above) binary distribution, expand it and place following JAR files under jar/log4j:
        - log4j-core-2.14.1.jar

# Reference
1. Ryuichiro Higashinaka, Masahiro Araki, Hiroshi Tsukahara and Masahiro Mizukami (2021),  [*Integrated taxonomy of errors in chat-oriented dialogue systems*](https://aclanthology.org/2021.sigdial-1.10/), in Proceedings of the 22nd Annual Meeting of the Special Interest Group on Discourse and Dialogue, p.89-98.
1. The annotation manuals (in Japanese and English) - [Integrated taxonomy of errors in chat-oriented dialogue systems](https://github.com/ryuichiro-higashinaka/taxonomy-of-errors)

# License
©2021 DENSO IT Laboratory, Inc., All rights reserved. Redistribution or public display not permitted without written permission from DENSO IT Laboratory, Inc.