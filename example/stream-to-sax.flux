"records.formeta"
| open-file
| as-lines
| decode-formeta
| stream-to-sax
| handle-generic-xml("record")
| encode-formeta
| print;