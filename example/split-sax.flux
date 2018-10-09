"records.xml"
| open-file
| decode-xml
| split-sax
| handle-generic-xml("record")
| stream-to-xml
| print;