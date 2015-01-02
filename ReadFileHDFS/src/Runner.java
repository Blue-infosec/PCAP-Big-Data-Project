import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.elasticsearch.hadoop.mr.EsOutputFormat;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class Runner {

	public static class MyMapper extends Mapper<Object, Text, NullWritable, Text> {

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
 			String filepath = value.toString();
 
 			// Get HDFS path of the file
 			Path pt = new Path(filepath);
 			
 			// Get the local path of the file
 			String PCAP_file_path = makeFileFromPath(pt, new Configuration());
 			
 			// Prepare pcap reader
 			Parser parser = new Parser();
 			
 			parser.parse(PCAP_file_path, context);
 			
			
		}

		/**
		 * Copy the file to local file system
		 * @param some_path
		 * path of the file on HDFS system
		 * @param conf
		 * Hadoop configuration
		 * @return 
		 * path of the file on local file system
		 * @throws IOException
		 */
		public String makeFileFromPath(Path some_path, Configuration conf)
				throws IOException {
			FileSystem fs = FileSystem.get(some_path.toUri(), conf);
			File temp_data_file = File.createTempFile(some_path.getName(), "");
			temp_data_file.deleteOnExit();
			fs.copyToLocalFile(some_path,
					new Path(temp_data_file.getAbsolutePath()));
			return temp_data_file.getAbsolutePath();
		}
	}


	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
            System.out.println("usage: [es-host:port] [input]");
            System.exit(-1);
        }
		//	Prepare configuration 
		Configuration conf = new Configuration();
		conf.set("es.input.json", "yes");
		conf.setBoolean("mapred.map.tasks.speculative.execution", false);    
		conf.setBoolean("mapred.reduce.tasks.speculative.execution", false);
		
		// Set server address
		conf.set("es.nodes", args[0]);
		
		// Set index/type
		conf.set("es.resource", "network/pcaps");
		
		//	Prepare the mapreduce job
		Job job = new Job(conf);
		
		// Prepare the native library for jnetpcap
		job.addCacheFile(new Path("/user/liutuo/libjnetpcap.so").toUri());
		job.setInputFormatClass(WholeFileInputFormat.class);
		job.setJarByClass(Runner.class);
		job.setMapperClass(MyMapper.class);
		job.setOutputFormatClass(EsOutputFormat.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(0);
		
		// Read arguments
		FileInputFormat.addInputPath(job, new Path(args[1]));
		
		/*Creating Filesystem object with the configuration*/
		FileSystem fs = FileSystem.get(conf);
		/*Check if output path (args[1])exist or not*/
		if(fs.exists(new Path("tmp"))){
		   /*If exist delete the output path*/
		   fs.delete(new Path("tmp"),true);
		}
		FileOutputFormat.setOutputPath(job, new Path("tmp"));

		// Start the mapreduce job
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}