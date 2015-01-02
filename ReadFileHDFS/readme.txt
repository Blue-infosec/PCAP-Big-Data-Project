1. Setup Hadoop (HDFS and MapReduce)

2. Run start_all.sh

3. Put pcap files to HDFS
	hadoop -put <LOCAL PATH TO PCAP FILES> <PATH IN HDFS FOR PCAP FILES>
	example:
		hadoop -put 
4. Put jnetpcap native library (libjnetpcap.so) to HDFS
	hadoop fs -put <LOCAL PATH TO libjnetpcap.so> <PATH IN HDFS FOR libjnetpcap.so>
	example:
		hadoop fs -put /home/puaykai/Downloads/jnetpcap-1.3.0/libjnetpcap.so /

5. Configure the native library hdfs path in the main function

6. git clone https://github.com/puaykaipoh/PCAP-Big-Data-Project.git, export the project to a Runnable jar

7. Run the jar :
	hadoop jar <YOUR EXPORTED JAR FILE>.jar <es-host:port> <input> <native library path>
	example:
		hadoop jar ReadFileHDFS.jar localhost:9200 /orange/orange.1.5.cap /libjnetpcap.so

8. Download and unzip elastic search from : http://www.elasticsearch.org/overview/elkdownloads/elasticsearch-1.4.2.tar.gz

9. Append the following lines in elasticsearch-1.4.2/config/elasticsearch.yml:

	http.cors.allow-origin: http://localhost
	http.cors.enabled: true
	
10.Download and unzip kibana from : https://download.elasticsearch.org/kibana/kibana/kibana-3.1.2.tar.gz

11.Change the line in  from kibana-3.1.2/config.js:
	elasticsearch: "http://"+window.location.hostname+":9200",
	to
	elasticsearch: "http://localhost:9200",
	
12.cd to elasticsearch-1.4.2/bin/ and then ./elasticsearch

13.Go to a browser, http://localhost:9200 and ENJOY!