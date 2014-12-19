import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Runner {

	public static class MyMapper

	extends Mapper<Object, Text, Text, IntWritable> {


		public void map(Object key, Text value, Context context)

		throws IOException, InterruptedException {

			String filepath = value.toString();
			
			//Get path of the file
			Path pt = new Path(filepath);
			//Get BufferedReader
			FileSystem fs = FileSystem.get(new Configuration());
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fs.open(pt)));


		}

	}

//	public static class MyReducer
//
//	extends Reducer<Text, IntWritable, Text, IntWritable> {
//
//		public void reduce(Text key, Iterable<IntWritable> values,
//
//		Context context
//
//		) throws IOException, InterruptedException {
//
//			for (IntWritable val : values) {
//
//				context.write(key, val);
//
//			}
//
//		}
//
//	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		Job job = new Job(conf, "Whole file input format");
		
		job.setInputFormatClass(WholeFileInputFormat.class);
		job.setJarByClass(Runner.class);
		conf.setBoolean("mapreduce.map.speculative", false);
		job.setMapperClass(MyMapper.class);
//		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);

		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}