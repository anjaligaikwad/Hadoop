//import java.io.*;
import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;



public class variance{
	public static class Mapperx extends Mapper<LongWritable,Text,Text,FloatWritable>{
		public void map(LongWritable key,Text value,Context context) 
//				throws IOException InterruptedException {
		{
			try{
				String[] record=value.toString().split(",");
				
			String stockSymbol = record[1];
			Float high = Float.valueOf(record[4]);
			Float low = Float.valueOf(record[5]);
			Float Var = ((high-low) * 100) / low;
			context.write(new Text (stockSymbol), new FloatWritable(Var));
			
		}catch(Exception e){
			e.getMessage();
		}
		
		}
	}
		public class VarReducer extends Reducer<FloatWritable,Text,Text,FloatWritable>{
			
				public void reduce(Text Key, Iterable<FloatWritable> values,Context context) throws IOException, InterruptedException{
					FloatWritable maxVal = new FloatWritable();
					float maxPercentValue=0.0f;
					float temp_val=0.0f;

					for (FloatWritable val : values)
					{       	
						temp_val = val.get(); 
						if(temp_val > maxPercentValue){
							maxPercentValue = temp_val;
						}
					}
					maxVal.set(maxPercentValue);
					context.write(Key, maxVal);
				}
			}
	

		public static void main(String[] args) throws Exception {
		    Configuration conf = new Configuration();
		    //conf.set("name", "value")
		    
		    Job job = Job.getInstance(conf, "Volume Count");
		    job.setJarByClass(variance.class);
		    job.setMapperClass(Mapperx.class);
		    //job.setCombinerClass(ReduceClass.class);
		    job.setReducerClass(VarReducer.class);
		    //job.setNumReduceTasks(0);
		    job.setOutputKeyClass(Text.class);
		    job.setOutputValueClass(LongWritable.class);
		    FileInputFormat.addInputPath(job, new Path(args[0]));
		    FileOutputFormat.setOutputPath(job, new Path(args[1]));
		    System.exit(job.waitForCompletion(true) ? 0 : 1);
		  }
}