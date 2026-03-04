if [ "$DSP_ENVTYPE" = "prod" ] ; then
    curl http://169.254.169.254/latest/meta-data/local-ipv4 > /tmp/ipaddr.txt
    echo -n export DD_AGENT_HOST= > /usr/local/bin/setenv.sh
    cat /tmp/ipaddr.txt >> /usr/local/bin/setenv.sh
    echo "" >> /usr/local/bin/setenv.sh
    echo export DD_ENV=$DSP_ENVTYPE >> /usr/local/bin/setenv.sh
    echo export DD_SERVICE=$SERVICE_NAME-$SERVICE_VERSION >> /usr/local/bin/setenv.sh
    echo export DD_TRACE_SAMPLE_RATE=1.0 >> /usr/local/bin/setenv.sh
    echo export DD_TAGS=type:connector,cu:$CU >> /usr/local/bin/setenv.sh
    echo export JAVA_AGENT="-javaagent:$JAVA_HOME/lib/dd-java-agent.jar" >> /usr/local/bin/setenv.sh
    chmod 755 /usr/local/bin/setenv.sh
    . /usr/local/bin/setenv.sh
else
    echo '## Datadog APM is not install in this environment' > /usr/local/bin/setenv.sh
    echo export JAVA_AGENT="" >> /usr/local/bin/setenv.sh
    chmod 755 /usr/local/bin/setenv.sh
    . /usr/local/bin/setenv.sh
fi