To execute the python code, we need to give this command:
	python basicFeatures.py
Assumptions: The name of the input file is test. The output file generated is basicFeatures_test.txt

Time taken : approx it will take 5min to generate feature vector for 450mb data.
================================================================================================================

To generate the model first you need to install the vowpal wabbit.

Following are the steps for the installation of vw:

	git clone git://github.com/JohnLangford/vowpal_wabbit.git

Now we compile:

	cd vowpal_wabbit
	make

If it fails, you most likely need to install the boost program options headers and library.

Boost installation on Debian/Linux distributions:

	sudo apt-get install libboost-program-options-dev



(above steps will take few minutes to intall)
================================================================================================================

After installation execute the following command:

vw --adaptive --l2 0.8e-8 -d outputFile.txt --readable_model basicFeature.model

where 	outputFile.txt contains the feature vector
	basicFeature.model is the output of vw which return the value of all the feature. Further these values will be used to re-rank the URLs.


Time taken: vw command will take approx 23mins for 2.2GB input feature vector file.
================================================================================================================