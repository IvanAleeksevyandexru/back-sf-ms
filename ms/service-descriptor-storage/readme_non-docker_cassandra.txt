Non-docker Cassandra

1. Download https://www.apache.org/dyn/closer.lua/cassandra/3.11.8/apache-cassandra-3.11.8-bin.tar.gz
2. Unpack tar.gz file
3. Change endpoint_snitch to GossipingPropertyFileSnitch in conf/cassandra.yaml. Example:
endpoint_snitch: GossipingPropertyFileSnitch
4. Check setting: dc and rack in conf/cassandra-rackdc.properties. dc must be dc1. Example:
dc=dc1
rack=rack1
5. Run cassandra.bat in bin folder
6. Install Python 2.7
7. Run Cassandra Console cqlsh.bat (or cqlsh.py) in bin folder.
8. Execute <project>/.dev/schema.cql in Cassandra Console
9. You can run the project application
