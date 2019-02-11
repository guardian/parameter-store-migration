package services

import com.amazonaws.auth.AWSCredentialsProviderChain
import com.amazonaws.auth.profile.ProfileCredentialsProvider

object CredentialsProvider {
  def apply(profile: String): AWSCredentialsProviderChain = new AWSCredentialsProviderChain(
    new ProfileCredentialsProvider(profile)
  )
}
