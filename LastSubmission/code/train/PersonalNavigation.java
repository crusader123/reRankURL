
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipOutputStream;

public class PersonalNavigation
{
	public static void main(String args[])
	{
		String inputpath=args[0];
		String outputpath=args[1];
		Long starttime=System.currentTimeMillis();
		//reading of file
		try
		{
			//input file
			BufferedReader bfrread = new BufferedReader(new FileReader(inputpath+"train"));

			//output file
			File file = new File(outputpath+"train_output");
			PrintWriter ptrwrite = new PrintWriter(new FileWriter(file));

			/*FileOutputStream fos = null;
			ZipOutputStream zos = null;
			fos = new FileOutputStream(outputpath+"output.zip");
		    zos = new ZipOutputStream(fos);
			*/
			String str=null;
			String splitedstr[]=null;

			Integer userId=null;
			HashMap<Integer,ShortTermCounter> shorttermmap=null;
			HashMap<Integer,LongTermCounter> longtermmap=null;
			ArrayList<String> finalshorttermlist=null;
			ArrayList<String> basicfeaturelist=null;
			StringBuilder shortlongcombined=null;

			//C
			int urlid;
			int lastclickedindex;
			int type;
			int index;
			boolean clickedornot;
			HashMap<Integer,Integer> relevantURLs=null;

			//Q or T
			StringBuilder []basicfeatureperquery=null;
			ArrayList<Integer> urllist=null;
			Boolean isProcessingComplete[]=null;
			boolean mflag=true;
			int queryID;
			String termID[]=null;
			String urldomainsplit[]=null;
			int noOfurlperquery;

			//M
			int currentUserID=-1;
			String sessionId=null;

			//for relevance weight tag
			ArrayList<String> rwtlist=null;
			StringBuilder []rwtperquery=null;

			while (true)
			{
				if(mflag)
				{
				str = bfrread.readLine();
				//here if str is null it means there is no query in this session....
				if(str==null)
					{
						System.out.println("No query in the given session");
						break;
					}
				}

				splitedstr=str.split("\t");
				if(splitedstr[1].equals("M"))
				{
					sessionId=splitedstr[0];
					userId=Integer.parseInt(splitedstr[3]);
					shorttermmap=new HashMap<Integer, ShortTermCounter>();
					if(currentUserID!=userId)
					{
						//printing the previous users long term value
						if(longtermmap!=null)
						{
							//System.out.println("LongTerm Record");
							/*Iterator<?> it = longtermmap.entrySet().iterator();
							while (it.hasNext()) 
							{
							    Map.Entry<Integer, LongTermCounter> entry = (Map.Entry) it.next();
							    Integer key = (Integer)entry.getKey();
							    LongTermCounter ltc = (LongTermCounter)entry.getValue();
							    System.out.println("url" + key + ":" + ltc);
							}*/

							//merging of short and long term results
							int url;
							int basicindex=0;
							String prefix="";
							for(String shortterm:finalshorttermlist)
							{
								url=Integer.parseInt(shortterm.substring(0, shortterm.indexOf(":")));

								shortlongcombined.append(prefix+rwtlist.get(basicindex)+" |"+
										basicfeaturelist.get(basicindex)+
											shortterm.substring(shortterm.indexOf(":")+1, shortterm.length())+
												longtermmap.get(url).toString());

								//System.out.println(basicfeaturelist.get(basicindex));
								//System.out.println(shortterm);

								prefix="\n";
								basicindex++;
							}
							//com.ning.compress
							ptrwrite.println(shortlongcombined);
							//zos.write(shortlongcombined.);
						}

						longtermmap=new HashMap<Integer, LongTermCounter>();
						finalshorttermlist=new ArrayList<String>();
						shortlongcombined=new StringBuilder();
						basicfeaturelist=new ArrayList<String>();
						rwtlist=new ArrayList<String>();
						relevantURLs=new HashMap<Integer,Integer>();
					}
					currentUserID=userId;
					mflag=true;
				}
				else if(splitedstr[2].equals("Q") || splitedstr[2].equals("T"))
				{
					mflag=false;
					urllist=new ArrayList<Integer>();
					noOfurlperquery=splitedstr.length-6;
					isProcessingComplete=new Boolean[noOfurlperquery];
					clickedornot=false;

					basicfeatureperquery=new StringBuilder[noOfurlperquery];
					for(int i=0;i<noOfurlperquery;i++)
						basicfeatureperquery[i]=new StringBuilder();

					//rwt
					rwtperquery=new StringBuilder[noOfurlperquery];
					for(int i=0;i<noOfurlperquery;i++)
						rwtperquery[i]=new StringBuilder();

					queryID=Integer.parseInt(splitedstr[4]);
					termID=splitedstr[5].split(",");

					lastclickedindex=-1;

					for(int i=6;i<splitedstr.length;i++)
						{
							urldomainsplit=splitedstr[i].split(",");
							urllist.add(Integer.parseInt(urldomainsplit[0]));
							
							rwtperquery[i-6].append("-1");
							
							/*if(!relevantURLs.containsKey(Integer.parseInt(urldomainsplit[0])))
									{
										relevantURLs.put(Integer.parseInt(urldomainsplit[0]), 0);
										rwtperquery[i-6].append("-1");
										//System.out.println("xyz");
									}*/
							
							if(relevantURLs.containsKey(Integer.parseInt(urldomainsplit[0])))
							{
								int rel=relevantURLs.get(Integer.parseInt(urldomainsplit[0]));
								
								if(rel==1)
									{
									rwtperquery[i-6]=new StringBuilder();
									rwtperquery[i-6].append("1 1");
									}
								else if(rel==2)
									{
									rwtperquery[i-6]=new StringBuilder();
									rwtperquery[i-6].append("1 2");
									}		
								else if(rel==3)
									{
									rwtperquery[i-6]=new StringBuilder();
									rwtperquery[i-6].append("1 3");
									}
							}
							else
							{
								relevantURLs.put(Integer.parseInt(urldomainsplit[0]), 0);
							}
							
							isProcessingComplete[i-6]=false;

							basicfeatureperquery[i-6].append("a "+(i-5));

							basicfeatureperquery[i-6].append(" |b "+queryID+" |t");
							for(String trm:termID)
								basicfeatureperquery[i-6].append(" "+trm);

							basicfeatureperquery[i-6].append(" |c "+urldomainsplit[0]);
							basicfeatureperquery[i-6].append(" |d "+urldomainsplit[1]);
							basicfeatureperquery[i-6].append(" |e "+userId);
							basicfeatureperquery[i-6].append(" |f "+urldomainsplit[0]+queryID);
							basicfeatureperquery[i-6].append(" |g "+urldomainsplit[0]+(i-5));
							basicfeatureperquery[i-6].append(" |h "+urldomainsplit[1]+(i-5));
							/*
							basicfeatureperquery[i-6].append(" |i "+urldomainsplit[0]);
							for(String trm:termID)
								basicfeatureperquery[i-6].append(trm);
							
							
							basicfeatureperquery[i-6].append(" |j "+urldomainsplit[1]);
							for(String trm:termID)
								basicfeatureperquery[i-6].append(trm);
							*/
							
						}

					for(StringBuilder s:basicfeatureperquery)
						basicfeaturelist.add(s.toString());

					while ((str = bfrread.readLine()) != null)
					{
						splitedstr=str.split("\t");
						if(splitedstr[2].equals("C"))
						{
							clickedornot=true;
							urlid=Integer.parseInt(splitedstr[4]);
							type=caculateTypeBasedOnDwell(Integer.parseInt(splitedstr[1]));
							//System.out.println(urllist);
							index=urllist.indexOf(urlid);

							if(index==-1)
									{
										//System.out.println("clicked URL does not displayed... Its horrible...!!!");
										//System.out.println("sessionID:"+sessionId);	
										continue;
									}
							
							//System.out.println(index);
							isProcessingComplete[index]=true;

							if(index>lastclickedindex)
									lastclickedindex=index;

							//shortterm
							//if present directly increment the count.. otherwise first insert and then increment
							if(!shorttermmap.containsKey(urlid))
								shorttermmap.put(urlid, new ShortTermCounter());

							//if already processed with better relevance level then continue
							if(isProcessingComplete[index])
							{
								if(rwtperquery[index].toString().startsWith("1 3"))
									continue;

								if(rwtperquery[index].toString().startsWith("1 2") && type==3)
									continue;
							}

							if(type==1)
								{
								shorttermmap.get(urlid).type1++;
								rwtperquery[index]=new StringBuilder();
								rwtperquery[index].append("1 3");
								
								relevantURLs.put(urlid, 3);
								//rwtperquery[index].delete(0, rwtperquery[index].length());
								//rwtperquery[index].append("1 3 'a");
								}
							if(type==2)
								{
								shorttermmap.get(urlid).type2++;
								rwtperquery[index]=new StringBuilder();
								rwtperquery[index].append("1 2");
								
								if(relevantURLs.get(urlid)<2)
									relevantURLs.put(urlid, 2);
								//rwtperquery[index].delete(0, rwtperquery[index].length());
								//rwtperquery[index].append("1 2 'a");
								}
							if(type==3)
								{
								shorttermmap.get(urlid).type3++;
								rwtperquery[index]=new StringBuilder();
								rwtperquery[index].append("1 1");
								
								if(relevantURLs.get(urlid)<1)
									relevantURLs.put(urlid, 1);
								//rwtperquery[index].delete(0, rwtperquery[index].length());
								//rwtperquery[index].append("1 1 'a");
								}

							//longterm
							//if present directly increment the count.. otherwise first insert and then increment
							if(!longtermmap.containsKey(urlid))
								longtermmap.put(urlid, new LongTermCounter());
							if(type==1)
								longtermmap.get(urlid).type1++;
							if(type==2)
								longtermmap.get(urlid).type2++;
							if(type==3)
								longtermmap.get(urlid).type3++;
						}
						else
							break;
					}

					for(StringBuilder s:rwtperquery)
					{
						//System.out.println(s.toString());
						rwtlist.add(s.toString());
					}


					for(int i=0;i<urllist.size();i++)
					{
						if(!isProcessingComplete[i])
						{
							if(lastclickedindex==-1)		// no url clicked.. hence all are missed
								type=4;
							else if(lastclickedindex>i)		//skipped
								type=5;
							else							//missed
								type=4;

							//shortterm
							//if present directly increment the count.. otherwise first insert and then increment						
							if(!shorttermmap.containsKey(urllist.get(i)))
								shorttermmap.put(urllist.get(i), new ShortTermCounter());
							if(type==4)
								shorttermmap.get(urllist.get(i)).type4++;
							if(type==5)
								shorttermmap.get(urllist.get(i)).type5++;

							//longterm
							//if present directly increment the count.. otherwise first insert and then increment						
							if(!longtermmap.containsKey(urllist.get(i)))
								longtermmap.put(urllist.get(i), new LongTermCounter());
							if(type==4)
								longtermmap.get(urllist.get(i)).type4++;
							if(type==5)
								longtermmap.get(urllist.get(i)).type5++;
						}
					}


					//print shortterm values for each URL of present query
					ShortTermCounter stprint;
					for(int url:urllist)
					{
						stprint=shorttermmap.get(url);
						//System.out.println("url:"+url+stprint);
						finalshorttermlist.add(url+":"+stprint);
					}

					//no more data in the input file
					if(str==null)
					{
						//print data for the last query here....
						//System.out.println("last");
						int url;
						int basicindex=0;
						String prefix="";
						for(String shortterm:finalshorttermlist)
						{
							url=Integer.parseInt(shortterm.substring(0, shortterm.indexOf(":")));

							shortlongcombined.append(prefix+rwtlist.get(basicindex)+" |"+
									basicfeaturelist.get(basicindex)+
										shortterm.substring(shortterm.indexOf(":")+1, shortterm.length())+
											longtermmap.get(url).toString());

							//System.out.println(basicfeaturelist.get(basicindex));
							//System.out.println(shortterm);

							prefix="\n";
							basicindex++;
						}
						ptrwrite.println(shortlongcombined);
						break;
					}


				}
			}

			//closing of output file
			ptrwrite.close();
		}
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		System.out.println(System.currentTimeMillis()-starttime);
	}

	public static int caculateTypeBasedOnDwell(int time)
	{
		if(time<50)						//relevace 0 means type 3
			return 3;	
		if(time>=50 && time<=399)		//relevance 1 means type  2	
			return 2;
		if(time>=400)					//relevance 2 means type 1
			return 1;
		return -1;
	}

}