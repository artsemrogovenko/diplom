function openModalEdit(button) {
    var myModal = new bootstrap.Modal(document.getElementById('editModal'));
    var id = button.getAttribute('c-id');
    var factoryNumber = button.getAttribute('c-factoryNumber');
    var model = button.getAttribute('c-model');
    var name = button.getAttribute('c-name');
    var quantity = button.getAttribute('c-quantity');
    var unit = button.getAttribute('c-unit');
    var description = button.getAttribute('c-description');
    var refill = button.getAttribute('c-refill');


    if(refill === 'true'){
        document.getElementById('comp_edit_refill').checked = true;
    }else {
        document.getElementById('comp_edit_refill').checked = false;
    }

    if (unit != 'шт') {
        if (quantity <= 999) {
            unit = "мм";
        } else if (quantity > 999 && quantity <= 999999) {
            quantity = quantity / 1000;
            unit = "м";
        } else {
            quantity = quantity / 1000000;
            unit = "км";
        }
    }
    document.getElementById('comp_edit_id').value = id;
    document.getElementById('comp_edit_factoryNumber').value = factoryNumber;
    document.getElementById('comp_edit_model').value = model;
    document.getElementById('comp_edit_name').value = name;
    document.getElementById('comp_edit_quantity').value = quantity;
    document.getElementById('comp_edit_unit').value = unit;
    document.getElementById('comp_edit_description').value = description;

    myModal.show();
}

