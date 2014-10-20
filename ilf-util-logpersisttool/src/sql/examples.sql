/*
CREATE TABLE DATAPOWER.DP_MESSAGES
 (MESSAGE_ID   VARCHAR(100)     NOT NULL,
  MESSAGE  XML NOT NULL,
  MESSAGE_TYPE VARCHAR(3) NOT NULL,
  MESSAGE_DATE DATE NOT NULL,
  PRIMARY KEY(MESSAGE_ID));
 */

--insert into dp_messages values ('1','<XML>HELLO</XML>',2,current timestamp)
--select * from DATAPOWER.DP_MESSAGES;

--xquery 
--for $y in db2-fn:xmlcolumn('DATAPOWER.DP_MESSAGES.MESSAGE')/*:Envelope/*:Header/*:messageID return $y/text()

xquery 
for $message in db2-fn:xmlcolumn('DATAPOWER.DP_MESSAGES.MESSAGE')/*:Envelope return $message






