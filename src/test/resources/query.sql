SELECT
  *
FROM serviceoffering ser, jsonb_array_elements(ser.entity -> 'propertyGroups') AS propertyGroups,
  jsonb_array_elements(propertyGroups -> 'properties') AS properties
WHERE
  propertyGroups::jsonb @> '{"properties":[{"name": "CPU", "value": 4}, {"name": "RAM", "value": 2}, {"name": "HDD", "value": 10}]}'::jsonb