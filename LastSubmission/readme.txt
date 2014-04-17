---------------------------------------------------------------READ ME---------------------------------------------------------------

Assumptions:
1. Java is installed
2. Vowpal Wabbit is installed (Guidelines for installation can be found at https://github.com/JohnLangford/vowpal_wabbit/wiki/Tutorial)
3. There is enough space on the machine ~160 GB

--------------------------------------------------------------------------------------------------------------------------------------
Following are the steps for the installation of vw:

	git clone git://github.com/JohnLangford/vowpal_wabbit.git

Now we compile:

	cd vowpal_wabbit
	make

If it fails, you most likely need to install the boost program options headers and library.

Boost installation on Debian/Linux distributions:

	sudo apt-get install libboost-program-options-dev

(above steps will take few minutes to intall)

--------------------------------------------------------------------------------------------------------------------------------------

There are three components of our system:
1. Feature Extraction for Training
2. Feature Extraction for Testing
3. Training using Logistic Regression
4. Testing
5. Re-ranking

--------------------------------------------------------------------------------------------------------------------------------------

1. Feature Extraction for Training

There are three files for this module in the train folder- PersonalNavigation.java, LongTermCounter.java, ShortTermCounter.java
PersonalNavigation.java takes two arguments - the path of the input train file and the path of the output
LongTermCounter.java and ShortTermCounter.java are called from PersonalNavigation.java.

How to run:
javac *.java
java  PersonalNavigation /path/to/input/train/file /path/to/output/file

Output:
train_output

--------------------------------------------------------------------------------------------------------------------------------------

2. Feature Extraction for Testing
This module has similar structure and has to be run in the same way as the previous module. The code is in the test folder.

How to run:
javac *.java
java  PersonalNavigation /path/to/input/test/file /path/to/output/file

Output:
test_output

--------------------------------------------------------------------------------------------------------------------------------------

3. Training using Logictic Regression

How to run:

vw -d train_output  -f data.model  --loss_function logistic --adaptive --invariant --l2 0.8e-8

Output:
data.model

--------------------------------------------------------------------------------------------------------------------------------------

4. Testing

How to run:

vw -t -d test_output  -i data.model  --loss_function logistic --adaptive --invariant --l2 0.8e-8 -r prediction

Output:
prediction

--------------------------------------------------------------------------------------------------------------------------------------

5. Reranking

There are three files for this module- PersonalNavigation.java, LongTermCounter.java, ShortTermCounter.java. LongTermCounter.java and ShortTermCounter.java are called from PersonalNavigation.java.
PersonalNavigation.java takes two arguments - the path of the input files (prediction file and test file) and the path to the output reranked URL list.

How to run:
javac *.java
java  PersonalNavigation /path/to/input/test/file /path/to/input/prediction/file /path/to/output/file

Output:
finalRankedURL
--------------------------------------------------------------------------------------------------------------------------------------

