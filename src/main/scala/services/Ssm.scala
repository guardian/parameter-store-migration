package services

import com.amazonaws.auth.AWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.simplesystemsmanagement.model.{ParameterType, PutParameterRequest}
import com.amazonaws.services.simplesystemsmanagement.{AWSSimpleSystemsManagement, AWSSimpleSystemsManagementClientBuilder}

class Ssm(credentialsProvider: AWSCredentialsProviderChain) {
  private val ssmClient: AWSSimpleSystemsManagement =
    AWSSimpleSystemsManagementClientBuilder
      .standard()
      .withCredentials(credentialsProvider)
      .withRegion(Regions.EU_WEST_1)
      .build()

  def put(key: String, value: String): Unit = {
    val request = new PutParameterRequest()
        .withName(key)
        .withValue(value)
        .withType(ParameterType.SecureString)

    ssmClient.putParameter(request)
  }
}
