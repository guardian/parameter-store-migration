package services

import com.amazonaws.auth.AWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}

object S3 {
  def getObject(bucket: String, key: String, credentialsProvider: AWSCredentialsProviderChain): String = {
    val s3Client: AmazonS3 = AmazonS3ClientBuilder
      .standard()
      .withRegion(Regions.EU_WEST_1)
      .withCredentials(credentialsProvider)
      .build()

    val s3Object = s3Client.getObject(bucket, key)

    val stream = s3Object.getObjectContent
    val result = scala.io.Source.fromInputStream(stream).mkString
    stream.close()

    result
  }
}
