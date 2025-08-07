
assumptions: 
task DO NOT require TDD aproach
task DO NOT require any jUnit tests
task DO NOT require additional classes
timebox: task is designed to be implemented in 45 minutes max


task description part:

```
1. REST API: Asteroid Monitoring Station

2. Use the HTTP GET method to retrieve information from a database of asteroids. Query `https://jsonmock.hackerrank.com/api/asteroids` to find all the records. To use the search feature, one can add `/search` followed by the parameter and keyword, which is case insensitive. If the keyword exists in the parameter's value, it is included in the response. For example, `https://jsonmock.hackerrank.com/api/asteroids/search?parameter=(keyword)`. The query result is paginated and can be further accessed by appending `?page=num` to the query string, where `num` is the page number.

3. The response is a JSON object with the following five fields:
   1. `page`: The current page of the results. (Number)
   2. `per_page`: The maximum number of results returned per page. (Number)
   3. `total`: The total number of results. (Number)
   4. `total_pages`: The total number of pages with results. (Number)
   5. `data`: Either an empty array or an array of asteroids that match the criteria.

4. In `data`, each asteroid object has the following schema:
   1. `designation`: The name of the asteroid (String)
   2. `discovery_date`: The date of the discovery (String)
   3. `period_yr`: The rotation period of an asteroid in years (String)
   4. `pha`: Is the asteroid potentially hazardous? Its value is "Y", "N", or "n/a" (String)

5. Given the year of discovery and the value of `pha`, filter the results based on the given parameters and sort ascending on `period_yr`. In case of a tie, sort on `designation`, also ascending. Return the list of designations. If `period_yr` does not exist for an asteroid, assume its value as 0.

6. Function Description
   - Complete the function `asteroidMonitor` in the editor below.

7. `asteroidMonitor` has the following parameters:
   1. `integer year`: year of discovery
   2. `string pha`: either "Y" or "N," denoting if the asteroid is potentially hazardous.

8. Return
   - `string[]`: list of the planet names

9. Note: Please review the header in the code stub to see available libraries for API requests in the selected language. Required libraries can be imported to solve the question. Check our full list of supported libraries.

```



code part:

```
import java.io.*;

class Result {

    /*
     * Complete the 'asteroidMonitor' function below.
     *
     * The function is expected to return a STRING_ARRAY.
     *
     * The function accepts following parameters:
     * 1. INTEGER year
     * 2. STRING pha
     *
     * API URL: https://jsonmock.hackerrank.com/api/asteroids/search?parameter={keyword}&page={page_no}
     */

    public static List<String> asteroidMonitor(int year, String pha) {

    }
}

public class Solution {
```

warning: imports were folden - you dont see all imports that are proposed