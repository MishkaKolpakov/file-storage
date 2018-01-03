1. To perform load testing be sure that FileService works with MongoDB.
2. Script is responsible for testing upload and download endpoints, so you need to :
- add valid X-AUTH header to avoid 403 error
- add available file to perform upload method
- add available file uuid (which already contains in FileService) to download file
3. Add auservice.jmx file to jmeter/bin directory
4. Run from jmeter/bin command "jmeter -n -t authservice.jmx -l results.csv" to perform testing
5. Run from jmeter/bin command "jmeter -g results.csv -o {path to dashboard directory}"
6. Open {path to dashboard directory} and run index.html to see results.
