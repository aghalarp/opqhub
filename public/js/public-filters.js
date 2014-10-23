var filters = {
  textInputs: ["frequencyGt", "frequencyLt", "voltageGt", "voltageLt", "durationGt", "durationLt"],
  checkBoxes: ["requestFrequency", "requestVoltage", "requestIticSevere", "requestIticModerate", "requestIticOk"],
  timeRanges: ["startTimestamp", "stopTimestamp"],
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
    requestIticOk: true,
    startTimestamp: 0,
    stopTimestamp: 10000000
  },

  toId: function(filter) {
    return "#" + filter;
  },

  init: function() {
    filters.timeRanges.map(function(filter) {
       $(filters.toId(filter)).datetimepicker();
    });
  },

  setFilter: function(filter, val) {
    var filterId = filters.toId(filter);
    // Determine type of filter
    if(filters.textInputs.indexOf(filter) > -1) {
      $(filterId).val(Math.round(val));
    }
    if(filters.checkBoxes.indexOf(filter) > -1) {
      $(filterId).prop('checked', val);
    }
    if(filters.timeRanges.indexOf(filter) > -1) {
      $(filterId).data("DateTimePicker").setDate(new Date(val));
    }
    if(filters.gridMap.indexOf(filter) > -1) {

    }
  },

  queryFilter: function(filter) {
    var filterId = filters.toId(filter);

    // Determine type of filter
    if(filters.textInputs.indexOf(filter) > -1) {
      return $(filterId).val();
    }
    if(filters.checkBoxes.indexOf(filter) > -1) {
      return $(filterId).is(':checked');
    }
    if(filters.timeRanges.indexOf(filter) > -1) {
      if($(filterId + "Input").val() === "") {
        return "";
      }
      else {
        return $(filterId).data("DateTimePicker").getDate()._d.getTime();
      }
    }
    if(filters.gridMap.indexOf(filter) > -1) {
      return map.gridMap.getVisibleIds();
    }
    return null;
  },

  updateDefaults: function(updatedDefaults) {
    filters.defaults = updatedDefaults;
    var filtersToUpdate = filters.textInputs.concat(filters.checkBoxes, filters.timeRanges);
    filtersToUpdate.map(function(filter) {
      if(filters.queryFilter(filter) === "") {
        filters.setFilter(filter, filters.defaults[filter]);
      }
    });
  },

  reset: function() {

    Object.keys(filters.defaults).map(function(filter){
      filters.setFilter(filter, filters.defaults[filter]);
    });
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
      startTimestamp: orDefault('startTimestamp'),
      stopTimestamp: orDefault('stopTimestamp'),
      visibleIds: filters.queryFilter('visibleIds')
    };
  }

};