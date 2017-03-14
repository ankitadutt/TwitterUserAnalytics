#Twitter Log Analysis Library

The code base contains a general-purpose library for Twitter log data analysis.
The library is used to provide a calculation for average user time.

#Approach:
The library takes an input log file with data format of the form {userId, timestamp, actionType} and breaks it into multiple shards.
The number of shards is determined by the number of distinct user(ids) and total available internal memory (both of which are accepted as optional user inputs)

A sharding service first divides the input file into consumable sized S shards, where S is calculated based on user provided inputs of: number of users and total available memory. In the absence of these parameters, the application uses default values from constants file:
MAX_MEMORY_IN_MB = 500;
MAX_NUMBER_OF_USERS = 100000;
NUM_SHARDS = 5;

*The assumption here is that user ids are evenly distributed and that each shard will contain a reasonable number of users only. However, if this is not the case, the application recursively shards the data during aggregation. *

While aggregating data within the service, if the set of users exceeds the capacity set by the library (based on the available memory provided by the user as an input),
the service will re-shard the data into another file, storing user logs which could not be considered while aggregating the current shard.
The main thread continues to execute as long as there are any unprocessed shards to ensure all the data has been aggregated.
At any given time, for each user only 6 values are stored:
•	userid
•	running total duration,
•	running total count of open/close entries
•	last recorded activity (operation timestamp and operation type)
•	userId as key of hashmap during aggregation

Two policies (optional user input) have been provided to handle missing log entries. These can be configured as: 'IGNORE_MISSING' or 'UPDATE_WITH_CURRENT' while executing the jar.

IGNORE_MISSING – this policy ignores the first misplaced log entry it receives. This means that the average time spent remains untouched when 2 consecutive ‘close’ operations are encountered. However, in case of 2 consecutive ‘open’ operations, the average time spent increases.

UPDATE_WITH_CURRENT – This policy assumes that the missing entry happened at the same time as the new entry and updates the total duration and total entries accordingly. For ex. if 2 consecutive open operations are encountered then the application assumes that the previous operation was closed at them same instant as this new open operation occurred.

Alternative Approach:
As an alternative approach, an implementation of external merge sort can be carried out before aggregating the data to calculate the average for a user.

#Assumptions:
•	Invalid entries such as missing userId or negative timestamps etc. are skipped
•	The application assumes that any input will always be as per the given format. If not, these entries are skipped.
•	The distribution of user ids is assumed to be even. Uneven distribution of ids has been handled by producing reshards of shards (this is an I/O expensive operation)
It is also assumed that the memory and number of users provided while executing the jar are valid values. This value is compared with the heap memory size but system properties are left untouched intentionally.

A sample input script to run the file is provided below:
java -jar TwitterDataMetrics.jar D:/generated.txt 100000 128 IGNORE_MISSING
java -jar TwitterDataMetrics.jar D:/testTwitter.txt 100000 128 IGNORE_MISSING


#Testing 
The code has been locally tested with a generated user load of 1 million users on 12MB memory
