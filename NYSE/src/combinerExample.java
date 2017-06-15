import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class combinerExample {
	
	
		public static class myMapper extends Mapper<LongWritable, Text, Text, Text>{
				
			public void map(LongWritable inpKey, Text inpVal, Context c) throws IOException, InterruptedException{
				String[] eachVal = inpVal.toString().split(",");
				int m1 =Integer.parseInt(eachVal[2]);
				int m2 =Integer.parseInt(eachVal[3]);
				int m3 =Integer.parseInt(eachVal[4]);
				int total = m1+m2+m3;
				float perc = total/3;
				c.write(new Text(eachVal[0]), new Text(Float.toString(perc)));
			}
		}
		
		
		public static class myCombiner extends Reducer<Text, Text, Text, Text>{
			
				public void reduce(Text inpCKey, Text inpCVal, Context c) throws IOException, InterruptedException{
					
					c.write(new Text("DummyKey"), inpCVal);
				}
			
		}
		
		
		public static class myReducer extends Reducer<Text, Text, Text, Text>{
			
			public void reduce(Text inpRKey, Text inpRVal, Context c) throws IOException, InterruptedException{
//					float max1 =0.0f, max2=0.0f;
//					for( Text  eachInp :inpRVal){
//						float perc = Float.parseFloat(eachInp.toString());
//						if(max1<perc){
//							max2=max1;
//							max1=perc;
//						}
//						if(max2<perc)
//							max2=perc;
//							
//					}
//					c.write(new Text("Top 2 Scorer are  :"), new Text(" 1. " +max1 +" 2 : "+max2));
//				
				c.write(inpRKey, inpRVal);
			}
			
		}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub
		
		Configuration conf = new Configuration();
		
		 Job j = Job.getInstance(conf,"Combiner Example");
		 
		 j.setJarByClass(combinerExample.class);
		 j.setMapperClass(myMapper.class);
		 j.setCombinerClass(myCombiner.class);
		 j.setReducerClass(myReducer.class);
		 j.setNumReduceTasks(1);
		 j.setOutputKeyClass(Text.class);
		 j.setOutputValueClass(Text.class);
		 FileInputFormat.addInputPath(j, new Path(args[0]));
		 FileOutputFormat.setOutputPath(j,new Path(args[1]));
		 
		 System.exit(j.waitForCompletion(true)?0:1);
		 
		 

	}

}
