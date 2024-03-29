package com.taka7646.rundeck.plugin

import spock.lang.Specification

class RundeckslackpluginSpec extends Specification {
    //Some Possible trigger names
    public static final String TRIGGER_START = "start";
    public static final String TRIGGER_SUCCESS = "success";
    public static final String TRIGGER_FAILURE = "failure";


    Map<String, Object> sampleExecutionData() {
        [
                id                     : 1,
                href                   : 'http://example.com/dummy/execution/1',
                status                 : 'succeeded',
                user                   : 'rduser',
                dateStarted            : new Date(0),
                'dateStartedUnixtime'  : 0,
                'dateStartedW3c'       : '1970-01-01T00:00:00Z',
                dateEnded              : new Date(10000),
                'dateEndedUnixtime'    : 10000,
                'dateEndedW3c'         : '1970-01-01T00:00:10Z',
                description            : 'a job',
                argstring              : '-opt1 value',
                project                : 'rdproject1',
                succeededNodeListString: 'nodea,nodeb',
                succeededNodeList      : ['nodea', 'nodeb'],
                loglevel               : 'INFO'
        ]
    }

    def "Post Notification basic success"() {
        given:

        Rundeckslackplugin plugin = new Rundeckslackplugin();
        //TODO: set additional properties for your plugin
        String trigger = TRIGGER_SUCCESS

        def executionData = sampleExecutionData()
        def configuration = [:]

        //TODO: add mock implementations of any objects which your plugin uses, such as HTTP clients, etc.

        when:

        def result = plugin.postNotification(trigger, executionData, configuration)

        then:
        result
    }

}