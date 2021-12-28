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
       total_mem
FROM host_info
GROUP BY cpu_number, id
ORDER BY total_mem DESC;

--Query 2: Average memory usage.
SELECT host_usage.host_id,
       round5(host_usage.timestamp),
       AVG(((host_info.total_mem - host_info.memory_free)/(host_info.total_mum))*100)
FROM host_usage,host_info
GROUP BY round5(host_usage.timestamp), host_usage.host_id

--Query 3: Detect host failure (less than 3 data points in a 5-min interval).
SELECT host_id,
       round5(timestamp),
       COUNT(*) AS num_data_points
FROM host_usage
GROUP BY host_id, round5(timestamp)
HAVING COUNT(*)<3
ORDER BY host_id