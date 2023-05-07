#!/usr/bin/env bash

# Set the name of the secret you want to retrieve
SECRET_NAME="urepair/jks"

# Retrieve the secret value from AWS Secret Manager
ALIAS_VALUE=$(aws secretsmanager get-secret-value --secret-id "$SECRET_NAME" --query "KEY_STORE_ALIAS" --output text)
KEY_VALUE=$(aws secretsmanager get-secret-value --secret-id "$SECRET_NAME" --query "KEY_STORE_PASSWORD" --output text)

# Set the environmental variable in the Elastic Beanstalk environment
echo "option_settings:
       aws:elasticbeanstalk:application:environment:
         KEY_STORE_ALIAS: $ALIAS_VALUE
         KEY_STORE_PASSWORD: $KEY_VALUE" >> .ebextensions/options.conf
