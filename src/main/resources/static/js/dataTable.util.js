function intVal(i) {
    return typeof i === 'string' ? i.replace(/[\$,]/g, '') * 1
        : typeof i === 'number' ? i : 0;
};

function sumColumn(api, columnIndex) {
    return api.column(columnIndex, {
        page : 'current'
    }).data().reduce(function(a, b) {
        return intVal(a) + intVal(b);
    }, 0);
}