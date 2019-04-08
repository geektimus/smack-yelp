create keyspace analysis with durable_writes = true
	and replication = {'class': 'org.apache.cassandra.locator.SimpleStrategy', 'replication_factor': '1'};

create table analysis.pet_friendly_restaurants
(
	id text primary key,
	business_name text,
	rating float,
	reviews int
)
with caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
	and compaction = {'max_threshold': '32', 'min_threshold': '4', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
	and compression = {'class': 'org.apache.cassandra.io.compress.LZ4Compressor', 'chunk_length_in_kb': '64'};

create table analysis.rating_with_price_range
(
	price_range text primary key,
	rating float
)
with caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
	and compaction = {'max_threshold': '32', 'min_threshold': '4', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
	and compression = {'class': 'org.apache.cassandra.io.compress.LZ4Compressor', 'chunk_length_in_kb': '64'};

create table analysis.user_review_sentiment
(
	user_id text primary key,
	count int,
	sentiment text
)
with caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
	and compaction = {'max_threshold': '32', 'min_threshold': '4', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
	and compression = {'class': 'org.apache.cassandra.io.compress.LZ4Compressor', 'chunk_length_in_kb': '64'};

