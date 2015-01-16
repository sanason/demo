<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- TODO Set up production maven profile that rewrites with minified resources. --%>

<!DOCTYPE html>
<html ng-app="expenseTrackerApp">
  <head>
    <meta charset="utf-8">
    <title>Expense Tracker</title>
    <link rel="stylesheet" href="<c:url value="/webjars/ng-grid/2.0.14/ng-grid.css"/>">
    <link rel="stylesheet" href="<c:url value="/webjars/bootstrap/3.3.1/css/bootstrap.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/app.css"/>">
    
    <!--
        In order for AngularJS to use jQuery instead of its own jQLite, we
        have to make sure jQuery is loaded first. ng-grid does not work
        with jQLite.
    -->
    <script src="<c:url value="/webjars/jquery/2.1.3/jquery.js"/>"></script>    
    <script src="<c:url value="/webjars/angularjs/1.3.8/angular.js"/>"></script>
    <script src="<c:url value="/webjars/ng-grid/2.0.14/ng-grid.js"/>"></script>   
    <script src="<c:url value="/webjars/angularjs/1.3.8/angular-route.js"/>"></script>
    <script src="<c:url value="/webjars/angular-ui-bootstrap/0.12.0/ui-bootstrap-tpls.js"/>"></script>

    <script>
      angular.module('config', [])
       .constant('ENV', {
          'apiEndpoint': "${apiEndpoint}"
          });
    </script>
    
    <script src="<c:url value="/js/app.js"/>"></script>
    <script src="<c:url value="/js/controllers.js"/>"></script>
  </head>
  <body>
    <div class="pageContent" ng-view></div>
  </body>
</html>