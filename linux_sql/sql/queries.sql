-- Helper function to round timestamp to 5 min.
CREATE FUNCTION round5(ts timestamp) RETURNS timestamp AS
    $$
BEGIN
    RETURN date_trunc('hour', ts) + date_part('minute', ts):: int / 5 * interval '5 min';
END;
$$
    LANGUAGE PLPGSQL;

-- Query 1: Group Hosts by hardware info.
SELECT cpu_number,
    id,
    total_mem,
    row_number() OVER(
        PARTITION BY cpu_number
        ORDER BY total_mem DESC
    )
    FROM host_info;

--Query 2: Average memory usage.
SELECT host_id,
       round5(host_usage.timestamp),
       trunc(avg(host_info.total_mem - host_info.memory_free ) / avg( host_info.total_mem ) * 100,1)
FROM host_info,host_usage
LEFT JOIN host_info
    ON host_usage.host_id=host_info.id
GROUP BY round5, host_usage.host_id

--Query 3: Detect host failure (<3 data points in a 5-min interval).
SELECT host_id,
       round5(host_usage.timestamp),
       COUNT(host_id) AS num_data_points
FROM host_usage
GROUP BY host_id, round5
HAVING count(host_id)<3