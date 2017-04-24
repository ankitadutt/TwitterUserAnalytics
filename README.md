TwitterAnalytics

Twitter Log Analysis Library

The code base contains a general purpose library for log data analysis.
The library is used to provide a calculation for average user time.
Approach:
The library takes an input file with data format of the form {userId, timestamp, actionType} and breaks it into multiple shards.
The number of shards is determined by the number of users and total available internal memory (both of which are optional user inputs)
A sharding service first divides the input file into consumable sized S shards, where S is calculated based on user provided inputs of - number of users and total available memory.
*The assumption here is that user ids are evenly distributed and that each shard will contain a reasonable number of users only*
While aggregating data within the service, if the set of users exceeds the capacity set by the library (based on the available memory provided by the user as an input),
the service will reshard the data into another file, storing user logs which could not be considered while aggregating the current shard.
The main thread continues to execute as long as there are any unprocessed shards to ensure all the data has been aggregated.
At any given time, for each user only 7 values are stored:
 userid,
 running total duration, 
 running total count of open/close entries
 last recorded activity (operation timestamp and operation type)

2 policies(optional user input) have been provided to handle missing user inputs. These can be used as: 'IGNORE_MISSING' or 'UPDATE_WITH_CURRENT' while executing the jar.



As an alternative approach, an implementation of external merge sort can be carried out before aggregating the data to calculate the average for a user.
The external merge sort will ensure that sorting does not consume internal memory. Merging the files will be the memory heavy part of the implementation.
The sorted output file will contain data sorted by user ids.
All the entries can be aggregated based on a running sum of duration and total entries.



Use (one) of the following script(s) to run the file:
java -jar TwitterDataMetrics.jar <filepath>generated.txt 100000 128 IGNORE_MISSING
java -jar TwitterDataMetrics.jar <filepath>testTwitter.txt 100000 128 IGNORE_MISSING
