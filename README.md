# confeus

A configuration file manager for Prometheus. At the moment, this only manages files_sd configs. This is a web app that is designed to be deployed on a Prometheus server.

## file_sd manager

Each file_sd config is managed per job. Targets per job are managed according to the labels.

The following format should be used for a file_sd config job:
```
- job_name: {job-name}
  file_sd_configs:
    - files:
        - files_sd/{job-name}.json
```

Updating a job's config
```
curl -XPUT http://localhost:3000/job/{job-name}/config/merge \
     --data '[{"targets": ["ip:host", "ip:host"], "labels": {"label1": value1}}, {"targets": ["ip:host", "ip:host"], "labels": {"label2": value2}}]' \
     --header "Content-type:application/json"
```

Setting a job's config
```
curl -XPUT http://localhost:3000/job/{job-name}/config/set \
     --data '[{"targets": ["ip:host", "ip:host"], "labels": {"label1": value1}}]' \
     --header "Content-type:application/json"
```

## Prerequisites

You will need [Leiningen][] 2.0.0 or above and JDK 8 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

Creating a jar
```
lein uberjar
```

Running the jar
```
java -jar target/confeus.jar --config-path /etc/prometheus/files_sd --port 3000
```

## License

MIT LICENSE
