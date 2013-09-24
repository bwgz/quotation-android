rem mvn -Dandroid.release=true clean generate-sources source:jar install
mvn install -P release -Dsign.keystore=/projects/keystore/bwgz.org.keystore -Dsign.storepass=8073449268 -Dsign.alias=org.bwgz.quotation -Dsign.keypass=8073449268
