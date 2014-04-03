import timeit
start = timeit.default_timer()
outputFile = open('basicFeatures_test.txt','w')
exitflag = False
with open('test','r') as inputFile:
	
	line = inputFile.readline()
	while 1:
		
		
		fields = line.strip().split('\t')
		if fields[1] == 'M':
			sessionID = fields[0]
			userID = fields[3]
			try:
				line = inputFile.next()
			except:
				break
			if line=="":
				break

		elif (fields[2] == 'Q' or fields[2] == 'T'):
			relevance = {}
			weight = {}
			url = []
			domainID = {}
			position = {}
			termIDs = {}
			queryID = fields[4]
			for index in range(6, len(fields)):
				urlDomain = fields[index].split(',')
				urlID = urlDomain[0]
				url.append(urlID)
				domainID[urlID] = urlDomain[1]
				position[urlID] = str(index - 5)
				termIDs[urlID] = fields[5].split(',')
				relevance[urlID] = '-1'
				weight[urlID] = '0'
			#print relevance
			try:
				line = inputFile.next()
			except:
				break

			while 1:
				
				if line=="":
					exitflag=True
					break
				fields = line.split('\t')
				if fields[2]=='C':
					clickedUrl = fields[4].strip()
					
					dwellTime = fields[1]
					dwellTime = int(dwellTime)
					if dwellTime < 50:
						relevance[clickedUrl] = '-1'
						weight[clickedUrl] = '0'
					elif dwellTime >= 50 and dwellTime < 400:
						

						relevance[clickedUrl] = '1'
						weight[clickedUrl] = '1'
					else:
						
						relevance[clickedUrl] = '1'
						weight[clickedUrl] = '2'
					
				else:
					break

				try:
					line = inputFile.next()
				except:
					break
			
			
			for urlID in url:				
				outputFile.write(relevance[urlID]+' '+weight[urlID]+" 'a "+'|'+' '+'1:'+position[urlID]+' '+'2:'+queryID)
				summation = 0
				outputFile.write(' '+'3:')
				for i in termIDs[urlID]:
					i = int(i)
					summation +=i
				outputFile.write(str(summation))
				
				outputFile.write(' '+'4:'+urlID+' '+'5:'+domainID[urlID]+' '+'6:'+urlID+queryID+' '+'7:'+urlID+position[urlID]+' '+'8:'+domainID[urlID]+position[urlID]+' '+'9:')
				outputFile.write(str(summation) + urlID+' ')
				outputFile.write('10:'+str(summation)+domainID[urlID]+' ')
				outputFile.write('11:'+userID+'\n')

			if exitflag:
				break	

end = timeit.default_timer()
print end - start				