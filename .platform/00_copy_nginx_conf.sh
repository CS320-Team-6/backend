#!/bin/bash

echo "Copying Nginx configuration file" >> /var/log/eb-custom-platform.log
cp .platform/nginx/https.conf /etc/nginx/conf.d/
echo "Finished copying Nginx configuration file" >> /var/log/eb-custom-platform.log
