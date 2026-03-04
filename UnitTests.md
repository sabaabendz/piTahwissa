vendor/bin/phpstan analyse --level=5 src tests

php bin/phpunit tests/Service/UserAuthServiceTest.php

php bin/phpunit --display-all-issues --display-phpunit-notices

