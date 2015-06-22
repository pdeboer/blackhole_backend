$(document).ready(function() {
    $('#usertable').dataTable( {
     "initComplete": function () {
                var api = this.api();
                api.$('td').click( function () {
                    api.search( this.innerHTML ).draw();
                } );
            }
    });
    $('#coordtable').dataTable();
    $('#tasklogtable').dataTable( {
        "initComplete": function () {
         var api = this.api();
         api.$('td').click( function () {
            api.search( this.innerHTML ).draw();
         } );
     }
     });
} );