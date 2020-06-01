package com.myapps.ds;

import java.util.List;

/*
we store the brokerNodes at this class
consumer uses this class to learn which brokers exist
 */
class ListOfBrokers {

    private List<BrokerNode> listOfBrokers;

    public ListOfBrokers() { }

    public List<BrokerNode> getListOfBrokers() {
        return listOfBrokers;
    }

    public void setListOfBrokers(List<BrokerNode> listOfBrokers) {
        this.listOfBrokers = listOfBrokers;
    }
}