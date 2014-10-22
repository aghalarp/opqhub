var filters = {
  textInputs: ["frequencyGt", "frequencyLt", "voltageGt", "voltageLt", "durationGt", "durationLt"],
  checkBoxes: ["requestFrequency", "requestVoltage", "requestIticSevere", "requestIticModerate", "requestIticOk"],
  timeRanges: ["startTimestampInput", "stopTimestampInput"],
  gridMap: ["visibleIds"],

  defaults: {
    frequencyGt: 0,
    frequencyLt: 100,
    voltageGt: 0,
    voltageLt: 300,
    durationGt: 0,
    durationLt: 1000,
    requestFrequency: true,
    requestVoltage: true,
    requestIticSevere: true,
    requestIticModerate: true,
    requestIticOk: true
  },

  toId: function(filter) {
    return "#" + filter;
  },

  init: function() {
    $('#startTimestamp').datetimepicker();
    $('#stopTimestamp').datetimepicker();
  },

  setFilter: function(filter, val) {
    // Determine type of filter
    if(filters.textInputs.indexOf(filter) > -1) {
      $(filters.toId(filter)).val(Math.round(val));
    }
    if(filters.checkBoxes.indexOf(filter) > -1) {
      return $(filters.toId(filter)).prop('checked', true);
    }
    if(filters.timeRanges.indexOf(filter) > -1) {

    }
    if(filters.gridMap.indexOf(filter) > -1) {

    }
  },

  queryFilter: function(filter) {
    // Determine type of filter
    if(filters.textInputs.indexOf(filter) > -1) {
      return $(filters.toId(filter)).val();
    }
    if(filters.checkBoxes.indexOf(filter) > -1) {
      return $(filters.toId(filter)).is(':checked');
    }
    if(filters.timeRanges.indexOf(filter) > -1) {
      var filterId = filters.toId(filter);
      switch(filter) {
        case "startTimestampInput":
          return $(filterId).val() == "" ?
            0 :
            $("#startTimestamp").data("DateTimePicker").getDate()._d.getTime();
          break;
        case "stopTimestampInput":
          return $(filterId).val() == "" ?
            new Date().getTime() :
            $("#stopTimestamp").data("DateTimePicker").getDate()._d.getTime();
          break;
      }
    }
    if(filters.gridMap.indexOf(filter) > -1) {
      return map.gridMap.getVisibleIds();
    }
    return null;
  },

  updateDefaults: function(updatedDefaults) {
    filters.defaults = updatedDefaults;
    filters.textInputs.map(function(filter) {
      if(filters.queryFilter(filter) === "") {
        filters.setFilter(filter, filters.defaults[filter]);
      }
    });
  },

  reset: function() {
    Object.keys(filters.defaults).map(function(filter){
      filters.setFilter(filter, filters.defaults[filter]);
    });
    $("#startTimestampInput").val(null);
    $("#stopTimestampInput").val(null);
  },

  toJson: function() {
    function orDefault(filter) {
      var val = filters.queryFilter(filter);
      return val === "" ? filters.defaults[filter] : val;
    }

    return {
      requestFrequency: filters.queryFilter('requestFrequency'),
      requestVoltage:   filters.queryFilter('requestVoltage'),
      requestHeartbeats: false,
      requestIticSevere: filters.queryFilter('requestIticSevere'),
      requestIticModerate: filters.queryFilter('requestIticModerate'),
      requestIticOk: filters.queryFilter('requestIticOk'),
      minFrequency: orDefault('frequencyGt'),
      maxFrequency: orDefault('frequencyLt'),
      minVoltage: orDefault('voltageGt'),
      maxVoltage: orDefault('voltageLt'),
      minDuration: orDefault('durationGt'),
      maxDuration: orDefault('durationLt'),
      startTimestamp: filters.queryFilter('startTimestampInput'),
      stopTimestamp: filters.queryFilter('stopTimestampInput'),
      visibleIds: filters.queryFilter('visibleIds')
    };
  }

};