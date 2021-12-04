## ❄️SNOWFLAKE-API WRITTEN IN JAVA❄️
snowflake-api is a Java based Twitter snowflake project. Its aim is to generate roughly sortable unique ids for distributed systems.

**64 bit id is composed of:**

✔️ 1 bit is reserved(currently set 0)
✔️ 41 bits for timestamp (millisecond precision with a custom epoch)
✔️ 5 bits  for worker id
✔️ 5 bits for datacenter id
✔️ 12 bits for sequence number that rolls over every 4096 per machine and avoids rolling over in the same millisecond.

📘 References:

- https://github.com/twitter/snowflake/tree/snowflake-2010
