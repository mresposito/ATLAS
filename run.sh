MAIN_CLASS=edu.illinois.learn.hadoop.YOURCLASS
OUT_DIR=results
INPUT_FILES="/Users/michele/data/12*"

sbt assembly
hadoop jar target/forumAnalysis-0.0.1.jar $MAIN_CLASS --hdfs --input $INPUT_FILES --output ~/junk/$OUT_DIR
say Done with Hadoop!
