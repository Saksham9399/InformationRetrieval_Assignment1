# InformationRetrieval_Assignment1
Assignment 1 based on Lucene Search Engine

Step 1:
Go to root in administrative mode by:-
`sudo su -`

Step 2:
change directory to assignment folder
`cd ~/Saksham_Assignment1/InformationRetrieval_Assignment1`


Step 3:
Do an mvn clean and install
` mvn clean install`

Step 4:
Run the driver program to run the code
`mvn exec:java -Dexec.mainClass="driver_program"`

if the driver programm throws and error detlete the index files from the root of the repository and do the whole process again.

Step 5: install trec eval
`git clone https://github.com/usnistgov/trec_eval.git`

Step 6 run make inside trec_eval toinstall files
`./trec_eval make`

Step 6:
Run trec eval to get the map scores
`./trec_eval/trec_eval QRelsCorrectedforTRECeval results.txt`   
