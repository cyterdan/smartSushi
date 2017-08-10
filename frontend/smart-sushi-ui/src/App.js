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
                <tr key={dish}>
                  <td>
                    {dish}
                  </td>
                  <td>
                    <input type="number" min="0" name={dish} defaultValue="0" onChange={this.props.onChange}></input>
                  </td>
                </tr>
                      );
      }
      return(
              <div className="Fleft" >
              Select how many you want for each dish : 
              <br />
              <table>
                <tbody>
                  {inputList}
                </tbody>
              </table>
               </div>
      );

      
    }
  
});
class Runner extends Component{


  constructor() {
    super();
    this.state = {
      bonus : [],
      order : [],
      price : 0,
      processing : false
    };
  }

  handle = (e) => {

      console.log("solving",this.props);
      this.setState({processing:true});
      $.ajax({
        type: "POST",
        url: this.props.source+"solve",
        data: {'menuId' : this.props.menuId,'requirements' : this.props.requirements},
        dataType: "json",
        success: (result) => {
              console.log(result);
              var orderList = [];
              for(var menuItem in result.order){
                orderList.push(
                    <tr key={menuItem}>
                        <td>
                            {menuItem}
                        </td>
                        <td>
                            {result.order[menuItem]}
                        </td>
                    </tr>
                  );
              }

              var bonusList = [];
              for(var bonusItem in result.bonus){
                bonusList.push(

                      <tr key={bonusItem}>
                          <td>
                              {bonusItem}
                          </td>
                          <td>
                              {result.bonus[bonusItem]}
                          </td>
                      </tr>


                  )

              }


              this.setState({order : orderList,price : result.price, bonus : bonusList,processing:false});
            }
      });
      
  }


render() {
  if(this.props.menuId){
    return (
      <div className="Runner">
          <button disabled={this.state.processing} onClick={this.handle}>{this.state.processing?"Working... ":"Find cheapest order !"}</button>
          
          {this.state.price ?
              <div>
          <h3>Results</h3>
          <h4>Total cost : {Math.round(this.state.price * 100) / 100} €</h4>

          <h4>Order</h4>
          <table>
            <thead>
                <tr>
                    <td> Menu item </td>
                    <td> Quantity </td>
               </tr>
            </thead>
            <tbody>
                 {this.state.order}
            </tbody>
          </table>

          {this.state.bonus.length>0? 
          <div>
            <h4>Bonus dishes</h4>
            <table>
              <thead>
                  <tr>
                      <td> Dish </td>
                      <td> Quantity </td>
                 </tr>
              </thead>
              <tbody>
                   {this.state.bonus}
              </tbody>
            </table>
          </div>
          :null}
          </div>

          :null}
      </div>
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
              <Runner source="http://localhost:8080/"  menuId={this.state.menuId} requirements={this.state.requirements} />
         </div>
    );
  }
}

export default App;
