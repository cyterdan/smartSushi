import React, { Component } from 'react';
import './App.css';
import $ from 'jquery'; 
import 'whatwg-fetch';
var createReactClass = require('create-react-class');


var MenuSelect = createReactClass({
  
  getInitialState: function() {
    return {
      menuList: []
    };
  },
  
  componentDidMount: function() {
    $.get(this.props.source+"menus", function(result) {
        var options = [];
        console.log(result[0]);
        for (var key in result[0]) {
              options.push(<option key={key} value={key}> {result[0][key]} </option>);
         }
     
            this.setState({
              menuList: options
            });
      
    }.bind(this));
  },

  handleChange(event) {
    
    var self = this;
    var menuId = event.target.value;
      $.get(this.props.source+"menu",{menuId : menuId}, function(result) 
      {
          self.props.onChange(menuId,result);
    });
    
    

  },


  render() {
    return (
        <label>
          <p>Choose an existing menu : </p>   
          <select value={this.state.value} onChange={this.handleChange}>
          <option value="0">none</option>
          {this.state.menuList}
          </select>
        </label>
    );
  }
});

var Requirements = createReactClass({
  
  
  
    render() {
      var inputList = []
      for(var i=0;i<this.props.dishes.length;i++){
          var dish = this.props.dishes[i];
          inputList.push(
                          <div>
                            <label key={dish}>
                                {dish} : <input type="number" name={dish} defaultValue="0" onChange={this.props.onChange}></input>
                            </label>
                            <br />
                          </div>
                          );
      }
      return(
              <div >
              Select how many you want for each dish : 
              <br />
              {inputList}
               </div>
      );

      
    }
  
});
class Runner extends Component{

  handle = (e) => {

      console.log("solving",this.props);
      $.ajax({
        type: "POST",
        url: this.props.source+"solve",
        data: {'menuId' : this.props.menuId,'requirements' : this.props.requirements},
        dataType: "json",
        success: function(result) {
              console.log(result);
            }
      });
      
  }


render() {
  if(this.props.menuId){
    return (
          <button onClick={this.handle}>Find cheapest order !</button>
     )
  }
  else{
    return (<div></div>)
  }
}

};



class App extends Component {
    
  constructor() {
    super();
    this.state = {
      dishes : [],
      requirements : {},
      menuId : null
    };
  }
  
  menuSelectHandler = (id,value) => {
    console.log("menu selected with",id,value);
      this.setState({
          dishes : value['dishes'],
          menuId : id
        });
  };
  
  dishAddedHandler = (event) => {
    var reqs = this.state.requirements;
    reqs[event.target.name] = event.target.value;
    this.setState({requirements:reqs});
    console.log(this.state.requirements);
  }


  render() {      
    return (
            <div>
                <MenuSelect source="http://localhost:8080/" onChange={this.menuSelectHandler}  />
                <br />
                <Requirements dishes={this.state.dishes} onChange={this.dishAddedHandler}></Requirements>
                <Runner source="http://localhost:8080/" menuId={this.state.menuId} requirements={this.state.requirements} />
         </div>
    );
  }
}

export default App;
